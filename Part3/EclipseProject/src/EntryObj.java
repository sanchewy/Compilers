//Object to contain an entry in the symbol table (implemented with a hash<String, EntryObj>)
public class EntryObj {
	String type;
	String value;

	public EntryObj() {

	}

	public EntryObj(String t, String v) {
		this.type = t;
		this.value = v;
	}

	@Override
	public String toString() {
		String s = "";
		if (this.type != null && !this.type.isEmpty()) {
			s += " type " + this.type;
		}
		if (this.value != null && !this.value.isEmpty()) {
			s += " value " + this.value;
		}
		return s;
	}
}
