package wdps;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Main {

	public static final StanfordCoreNLP pipeline = CoreNLPUtils.StanfordDepNNParser();
	// String text = "Michael Joseph Jackson [Michael Jackson][1][2] (August 29,
	// 1958 – June 25, 2009) was an American [United States] singer, songwriter, and
	// dancer. Dubbed the \"King of Pop\",[3][4] he was one of the most popular
	// entertainers in the world, and was the best-selling music artist at the time
	// of his death.[5][6] Jackson [Michael Jackson]'s contributions to music,
	// dance, and fashion[7][8][9] along with his publicized personal life made him
	// a global figure in popular culture for over four decades.";
	private static final Logger logger = LogManager.getLogger("Logger");

	public ArrayList<WDPSToken> resolve(String text) {
		ArrayList<WDPSToken> output = new ArrayList<WDPSToken>();
		ArrayList<WDPSToken> output2 = new ArrayList<WDPSToken>();
		ArrayList<WDPSToken> tokens = annotateText(text);
		HashSet<Integer> availableChainIDs = new HashSet<Integer>();
		ArrayList<WDPSDisambiguatedEntity> disAmEnti = null;
		try {
			disAmEnti = diambiguateEntity(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (WDPSToken token : tokens) {
			int tokenBeginPos = token.beginPosition;
			for (WDPSDisambiguatedEntity ent : disAmEnti) {
				if (token.endPosition <= (ent.getOffset() + ent.getLength()) && token.beginPosition >= ent.getOffset())
					token.setKbIdentifier(ent.getKbIdentifier());
			}
			output.add(token);
		}

		for (WDPSToken tok : output) {
			availableChainIDs.add(tok.getCorefID());
		}
		HashMap<Integer, String> out = new HashMap<Integer, String>();
		for (Integer chainID : availableChainIDs) {
			if (chainID == -1)
				continue;
			String KbIdentifier = "";
			for (WDPSToken t : output) {
				if (chainID == t.getCorefID() && t.getKbIdentifier() != "") {
					KbIdentifier = (t.getKbIdentifier());
				}
			}
			out.put(chainID, KbIdentifier);
		}
		for(WDPSToken tok : output) {
			String kb = out.get(tok.getCorefID());
			if(kb==null)
				kb="";
			tok.setKbIdentifier(kb);
			output2.add(tok);
		}
		
		return output2;

	}

	public ArrayList<WDPSToken> annotateText(String text) {
		Annotation document = null;
		try {
			document = new Annotation(text);
			pipeline.annotate(document);
		} catch (Exception e) {
		}
		ArrayList<WDPSToken> tokenList = new ArrayList<WDPSToken>();
		HashMap<Integer, HashMap<Integer, WDPSCorefMention>> corefMap = new HashMap<Integer, HashMap<Integer, WDPSCorefMention>>();
		List<CoreMap> coreMapSentences = document.get(SentencesAnnotation.class);
		int sentenceNumber = 1;
		for (CoreMap sentence : coreMapSentences) {
			// Get the tokens
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			for (CoreLabel t : tokens) {
				String word = t.word();
				String ner = t.ner();
				int beginPos = t.beginPosition();
				int endPos = t.endPosition();
				int index = t.index();
				WDPSToken token = new WDPSToken(index, word, ner, beginPos, endPos, sentenceNumber);
				tokenList.add(token);
			}
			sentenceNumber++;
		}

		Map<Integer, CorefChain> coref = document.get(CorefChainAnnotation.class);
		ArrayList<WDPSCorefMention> corefList = new ArrayList<WDPSCorefMention>();
		HashSet<Integer> availableChains = new HashSet<Integer>();
		for (Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
			CorefChain c = entry.getValue();
			List<CorefMention> mentions = c.getMentionsInTextualOrder();
			for (CorefMention m : mentions) {
				int sentNum = m.sentNum;
				int startIndex = m.startIndex;
				int endIndex = m.endIndex;
				int corefChain = c.getChainID();
				WDPSCorefMention corefMention = new WDPSCorefMention(sentNum, startIndex, endIndex, corefChain);
				corefList.add(corefMention);
				availableChains.add(corefChain);
			}
		}

		for (int i = 1; i <= sentenceNumber; i++) {
			HashMap<Integer, WDPSCorefMention> tempHashMap = new HashMap<Integer, WDPSCorefMention>();
			for (WDPSCorefMention c : corefList) {
				if (c.sentenceId == i) {
					tempHashMap.put(c.startIndex, c);
				}
			}
			corefMap.put(i, tempHashMap);
		}
		ArrayList<WDPSToken> outPutList = new ArrayList<WDPSToken>();
		int takeNextToken = 0;
		int lastCorefID = -1;
		for (WDPSToken tok : tokenList) {
			int sentNum = tok.getSentenceNumber();
			HashMap<Integer, WDPSCorefMention> tmpMap = corefMap.get(sentNum);

			WDPSCorefMention tmpMentino = tmpMap.get(tok.getIndex());
			if (tmpMentino != null) {
				takeNextToken = tmpMentino.getEndIndex() - tmpMentino.getStartIndex();
				lastCorefID = tmpMentino.getCorefChain();
			}

			if (takeNextToken > 0) {
				tok.setCorefID(lastCorefID);
				takeNextToken--;

			}
			outPutList.add(tok);
		}
		return outPutList;

	}

	public ArrayList<WDPSDisambiguatedEntity> diambiguateEntity(String input) throws IOException {
		String params = "text=" + input + "";
		ArrayList<WDPSDisambiguatedEntity> outputList = new ArrayList<WDPSDisambiguatedEntity>();
		String urly = "https://gate.d5.mpi-inf.mpg.de/aida/service/disambiguate";
		URL obj = new URL(urly);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();

		BufferedReader iny = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();

		while ((output = iny.readLine()) != null) {
			response.append(output);
		}
		iny.close();
		JSONObject wholeResponse = new JSONObject(response.toString());
		JSONArray allMentions = wholeResponse.getJSONArray("mentions");
		JSONObject entityMetadata = wholeResponse.getJSONObject("entityMetadata");
		for (int i = 0; i < allMentions.length(); i++) {
			JSONObject entry = (JSONObject) allMentions.get(i);
			int offset = (int) entry.get("offset");
			int length = (int) entry.get("length");
			String name = entry.getString("name");
			String kbIdentifier = "";
			JSONArray allEntities = entry.getJSONArray("allEntities");
			if (allEntities.length() > 0) {
				JSONObject bestEntity = (JSONObject) entry.get("bestEntity");
				kbIdentifier = entityMetadata.getJSONObject(bestEntity.getString("kbIdentifier")).getString("url");
			}

			WDPSDisambiguatedEntity disEntity = new WDPSDisambiguatedEntity(offset, length, name, kbIdentifier);
			outputList.add(disEntity);
		}
		return outputList;

	}
}
