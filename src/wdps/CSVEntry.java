package wdps;

public class CSVEntry {
	public String docID;
	public String word;
	public String kbId;
	
	
	public CSVEntry(String docID, String word, String kbId) {
		super();
		this.docID = docID;
		this.word = word;
		this.kbId = kbId;
	}
	public String getDocID() {
		return docID;
	}
	public void setDocID(String docID) {
		this.docID = docID;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getKbId() {
		return kbId;
	}
	public void setKbId(String kbId) {
		this.kbId = kbId;
	}
	
	
}
