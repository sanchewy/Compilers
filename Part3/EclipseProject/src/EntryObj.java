//Object to contain an entry in the symbol table (implemented with a hash<String, EntryObj>)
public class EntryObj {
	String name;
	String type;
	String value;

	public EntryObj() {

	}

	public EntryObj(String n, String t, String v) {
		this.name = n;
		this.type = t;
		this.value = v;
	}

	@Override
	public String toString() {
		String s = "";
		if (this.name != null && !this.type.isEmpty()) {
			s += "name " + this.name;
		}
		if (this.type != null && !this.type.isEmpty()) {
			s += " type " + this.type;
		}
		if (this.value != null && !this.value.isEmpty()) {
			s += " value " + this.value;
		}
		return s;
	}
}
