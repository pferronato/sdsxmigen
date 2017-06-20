package import_SDS_metamodels;

public class MetaModelRel {

	/**
	 * @param name
	 * @param from
	 * @param to
	 * @param foreignKey
	 * @param card
	 */
	public MetaModelRel(String name, String from, String to, String foreignKey, CardMMTypes card) {
		this.setName(name);
		this.setFrom(from);
		this.setTo(to);
		this.setForeignKey(foreignKey);
		this.setCardinality(card);
	}

	public MetaModelRel (){
		from = "";
		to = "";
		name = "";
		cardinality = CardMMTypes.UNDEFINED;
		foreignKey = "";
	}
	public enum CardMMTypes {
		CARDM1, CARD1M, CARDMM, UNDEFINED {
		}
	}

	private String from;
	private String to;
	private String name;
	private CardMMTypes cardinality;
	private String foreignKey;

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	public CardMMTypes getCardinality() {
		return cardinality;
	}

	public void setCardinality(CardMMTypes card) {
		this.cardinality = card;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String fromCls) {
		this.from = fromCls;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String toCls) {
		this.to = toCls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
