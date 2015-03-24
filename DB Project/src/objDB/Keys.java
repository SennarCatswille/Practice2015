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
	
	public Keys(int t, String n) {
		keyType = t;
		keyName = new String(n);
		
	}
	
	public Keys(int t, String n, String f) {
		keyType = t;
		keyName = new String(n);
		fKeyName = new String(f);
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
}
