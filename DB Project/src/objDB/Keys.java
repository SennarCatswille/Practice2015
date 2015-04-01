/**
 * Класс описания ключей таблиц
 */
package objDB;

/**
 * @author Кирилл
 *
 */
public class Keys {
	private int keyType; // 1 - primary, 2 - foreign
	private String keyName;
	private String fKeyName;
	private String scheme;
	private String pTable;
	private String fTable;
	
	public Keys(int t, String tab, String n, String schem) {
		keyType = t; //- t = 1
		pTable = new String(tab);
		keyName = new String(n);
		scheme = new String(schem);
	}
	
	public Keys(int t, String n, String pTab, String f, String fTab, String schem) {
		keyType = t; //- t = 2
		keyName = new String(n);
		pTable = new String(pTab);
		fKeyName = new String(f);
		fTable = new String(fTab);
		scheme = new String(schem);
	}
	
	public Keys(Keys k) {
		keyType = k.getKeyType();
		keyName = k.getKeyName();
		pTable = k.getPTable();
		fKeyName = k.getFKeyName();
		fTable = k.getFTable();
		scheme = k.getScheme();
	}
	
	public boolean equals(Keys obj) {
		if (this.keyType == obj.getKeyType()) {
			if (this.keyName.equals(obj.getKeyName())) {
				if (this.keyType == 2) {
					if (this.fKeyName.equals(obj.getFKeyName())) {
						return true;
					}
				} else return true;
			}
		}
		return false;
	}
	
	public int getKeyType() {
		return keyType;
	}
	
	public String getKeyName() {
		return keyName;
	}
	
	public String getFKeyName() {
		return fKeyName;
	}
	
	public String getScheme() {
		return scheme;
	}
	
	public String getPTable() {
		return pTable;
	}
	
	public String getFTable() {
		return fTable;
	}
}
