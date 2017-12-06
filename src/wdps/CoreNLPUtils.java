package wdps;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class CoreNLPUtils {
	
	public static StanfordCoreNLP StanfordDepNNParser() {
		Properties props = new Properties();

		props.put("language", "english");
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,mention,coref");
		props.put("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz");
		props.put("parse.originalDependencies", true);
		props.setProperty("ner.useSUTime", "false");
		props.setProperty("coref.algorithm", "statistical");
		props.setProperty("coref.maxMentionDistance", "30"); // default = 50
		props.setProperty("coref.maxMentionDistanceWithStringMatch", "250"); // default = 500
		// Probably not needed, since we don't train a new model.
		// But if, for some reason, a new model is trained this will reduce the memory
		// load in the training
		props.setProperty("coref.statistical.maxTrainExamplesPerDocument", "1100"); // Use this to downsample examples
																					// from larger documents. A value
																					// larger than 1000 is recommended.
		props.setProperty("coref.statistical.minClassImbalance", "0.04"); // A value less than 0.05 is recommended.

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		return pipeline;
	}
}
