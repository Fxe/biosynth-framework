package edu.uminho.biosynth.core.data.integration.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultGraphImpl;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLinkType;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbEntityDictionary;
import edu.uminho.biosynth.core.data.integration.references.IReferenceTransformer;
import edu.uminho.biosynth.core.data.service.IMetaboliteService;

public class ReferenceLoader<T extends GenericMetabolite, R extends GenericCrossReference> implements IReferenceLoader{
	
	private final static Logger LOGGER = Logger.getLogger(ReferenceLoader.class.getName());
	
	private Class<T> entityClazz;
	private Class<R> entityReferenceClazz;
	private IReferenceTransformer<R> referenceTransformer;
	public IReferenceTransformer<R> getReferenceTransformer() { return referenceTransformer;}
	public void setReferenceTransformer(IReferenceTransformer<R> referenceTransformer) { this.referenceTransformer = referenceTransformer;}

	private IMetaboliteService<T> service;
	public IMetaboliteService<T> getService() { return service;}
	public void setService(IMetaboliteService<T> service) { this.service = service;}
	
	public ReferenceLoader(Class<T> entityClazz, Class<R> entityReferenceClazz, 
			IReferenceTransformer<R> referenceTransformer) {
		this.entityClazz = entityClazz;
		this.entityReferenceClazz = entityReferenceClazz;
		this.referenceTransformer = referenceTransformer;
	}
	
	@Override
	public List<String> getMetabolitesId() {
		return service.getAllMetabolitesEntries();
	}
	
	public IBinaryGraph<ReferenceNode, ReferenceLink> getMetaboliteReferences(String entry) {
		LOGGER.log(Level.INFO, "BUILDING REFERENCES FOR " + entry); 
		// returns a graph with metabolite and references
		// this graph should be merged with main reference graph
		IBinaryGraph<ReferenceNode, ReferenceLink> referenceGraph = 
				new DefaultGraphImpl<>();
				
		// querys the metabolite from the service
		T metabolite = this.service.getMetaboliteByEntry(entry);
		
		// builds the main node (the metabolite T it self) 
		ReferenceNode selfNode = new ReferenceNode(metabolite.getEntry().toUpperCase(), entityClazz);
		selfNode.setEntryTypePair(selfNode.getEntryTypePair());
		selfNode.addIdServicePair(0, this.service.getServiceId());
		referenceGraph.addVertex(selfNode);
		
		// atempts to invoke the getCrossReferences method
		Method crossreferenceMethod;
		LOGGER.log(Level.INFO, "CREATED NODE " + selfNode.getEntryTypePair().getFirst() + " X " + selfNode.getEntryTypePair().getSecond().getSimpleName());
		try {
			crossreferenceMethod = metabolite.getClass().getMethod("getCrossReferences");
			@SuppressWarnings("unchecked")
			Collection<Object> xrefObjSet = (Collection<Object>) crossreferenceMethod.invoke(metabolite);
			
			for (Object xrefObj :  xrefObjSet) {
				
				// checks if the element xrefObj matches the entityReferenceClazz
				if (xrefObj.getClass().equals(entityReferenceClazz)) {
					
					// apply the transformer
					GenericCrossReference xref = referenceTransformer.transform(entityReferenceClazz.cast(xrefObj));
					
					// turns the xref into reference node (graph edge)
					ReferenceNode xrefNode = transformReferneceToNode(xref);
					
					if (xrefNode != null) {
						LOGGER.log(Level.INFO, "CREATED NODE " + xrefNode.getEntryTypePair().getFirst() + " X " + xrefNode.getEntryTypePair().getSecond().getSimpleName());
						referenceGraph.addVertex(xrefNode);
						ReferenceLink refLink = new ReferenceLink();
						refLink.setLinkId(selfNode.toString() +  " -> " + xrefNode.toString());
						refLink.setLinkName("unamed");
						refLink.setType(ReferenceLinkType.SAMEAS);
						IBinaryEdge<ReferenceLink, ReferenceNode> edge = 
								new DefaultBinaryEdge<>(refLink, selfNode, xrefNode);
						referenceGraph.addEdge(edge);
						LOGGER.log(Level.INFO, "CREATED LINK " + selfNode.getEntryTypePair().getFirst() + " -> " + xrefNode.getEntryTypePair().getFirst());
					}
				}
//				System.out.println(xref);
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.INFO, e.getMessage());
		}
		
		return referenceGraph;
	}
	
	private ReferenceNode transformReferneceToNode(GenericCrossReference xref) {
		ReferenceNode xrefNode = null; 
		
		switch (xref.getType()) {
			case DATABASE:
				Class<?> type = BioDbEntityDictionary.getGenericMetaboliteFromString(xref.getRef());
				if (type == null) {
					LOGGER.log(Level.SEVERE, "Unable to determine entity type of - " + xref);
					return null;
				}
				xrefNode = new ReferenceNode(xref.getValue(), type);
				break;
			case MODEL:
//				xrefNode = new ReferenceNode(xref.getValue(), String.class);
				break;
			case ECNUMBER:
				LOGGER.log(Level.SEVERE, "Unsupported type - " + xref.getType());
				break;
			default:
				LOGGER.log(Level.SEVERE, "Unsupported type - " + xref.getType());
				break;
		}
		
		return xrefNode;
	}
}
