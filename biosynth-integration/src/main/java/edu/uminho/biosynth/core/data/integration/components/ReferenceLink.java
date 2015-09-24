package edu.uminho.biosynth.core.data.integration.components;

public class ReferenceLink {
	private String linkId;
	private String linkName;
	private ReferenceLinkType type;
	
	public String getLinkId() { return linkId;}
	public void setLinkId(String linkId) { this.linkId = linkId;}
	
	public String getLinkName() { return linkName;}
	public void setLinkName(String linkName) { this.linkName = linkName;}
	
	public ReferenceLinkType getType() { return type;}
	public void setType(ReferenceLinkType type) { this.type = type;}
	
	//Other stuff to mark the link
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof ReferenceLink) {
			ReferenceLink linkInstance = (ReferenceLink) obj;
			return linkId.equals(linkInstance.getLinkId());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return linkId.hashCode();
	}
	
	@Override
	public String toString() {
		return linkId.toString();
	}
}
