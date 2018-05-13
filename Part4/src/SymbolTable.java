import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


//I think this can be linear or a hash table, but Kihanda said that it should be a hash table...
//The symbol table keeps track of variable and function/procedure names and
//Apparently the "This symbol table data structure hierarchy is stored in the semantic analyzer"
public class SymbolTable {
	// A hash map storing entries of the form {key = symbol name, value = type}
	Map<String, EntryObj> st = new LinkedHashMap<String, EntryObj>();
	String tableName;

	// Constructor
	public SymbolTable(String tableName) {
		this.tableName = tableName;
	}

	// Insert a new symbol into the symbol table. This operation is UNPROTECTED and
	// should ONLY be done following a negative lookup() operation.
	public void insert(String key, EntryObj value) {
		st.put(key, value);
	}

	// Return true if the symbol already exists in the GIVEN SCOPE, otherwise return
	// false.
	public boolean lookup(String key) {
		if (st.get(key) != null) {
			return true;
		}
		return false;
	}

	// Print the symbol table to console
	public void prettyPrint() {
		System.out.println("Symbol table " + tableName);
		// According to the javadoc, LinkedHashMap.entrySet() should be in the order
		// that the elements were inserted initially.
		Iterator it = st.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getValue().toString());
			it.remove();
		}
		// Print blank line at end of symbol table.
		System.out.println();
	}
		public Set getvals() {
		//System.out.println("Symbol table " + tableName);
		// According to the javadoc, LinkedHashMap.entrySet() should be in the order
		// that the elements were inserted initially.
		Set <String> vals = new HashSet<String>();
		Iterator it = st.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			vals.add(pair.getValue().toString());
			it.remove();
		}
		// Print blank line at end of symbol table.
		return vals;
	}

}
