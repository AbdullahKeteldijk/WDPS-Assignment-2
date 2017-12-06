package wdps;

public class WDPSDisambiguatedEntity {
	public int offset;
	public int length;
	public String name;
	public String kbIdentifier;
	
	public WDPSDisambiguatedEntity(int offset, int length, String name, String kbIdentifier) {
		super();
		this.offset = offset;
		this.length = length;
		this.name = name;
		this.kbIdentifier = kbIdentifier;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKbIdentifier() {
		return kbIdentifier;
	}
	public void setKbIdentifier(String kbIdentifier) {
		this.kbIdentifier = kbIdentifier;
	}
	
	
}
