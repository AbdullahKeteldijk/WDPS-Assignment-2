package wdps;

public class WDPSToken {
	int index;
	String word;
	String ner;
	int beginPosition;
	int endPosition;
	int sentenceNumber;
	int corefID;
	String kbIdentifier;

	public WDPSToken(int index, String word, String ner, int beginPosition, int endPosition, int sentenceNumber) {
		super();
		this.index = index;
		this.word = word;
		this.ner = ner;
		this.beginPosition = beginPosition;
		this.endPosition = endPosition;
		this.sentenceNumber = sentenceNumber;
		corefID = -1;
		kbIdentifier = "";
	}
	public String toString() {
		String out = word+"--"+"ChainID: "+corefID+" --- KB: "+kbIdentifier;
		return out;
	}

	public int getCorefID() {
		return corefID;
	}

	public void setCorefID(int corefID) {
		this.corefID = corefID;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getNer() {
		return ner;
	}

	public void setNer(String ner) {
		this.ner = ner;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(int beginPosition) {
		this.beginPosition = beginPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public int getSentenceNumber() {
		return sentenceNumber;
	}

	public void setSentenceNumber(int sentenceNumber) {
		this.sentenceNumber = sentenceNumber;
	}

	public String getKbIdentifier() {
		return kbIdentifier;
	}

	public void setKbIdentifier(String kbIdentifier) {
		this.kbIdentifier = kbIdentifier;
	}
	

}
