package pt.uminho.sysbio.biosynth.integration.model;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

//import utilities.datastructures.collection.CollectionUtils;
//import utilities.grammar.syntaxtree.AbstractSyntaxTree;
//import utilities.grammar.syntaxtree.AbstractSyntaxTreeNode;
//import utilities.math.language.mathboolean.DataTypeEnum;
//import utilities.math.language.mathboolean.IValue;
//import utilities.math.language.mathboolean.node.And;
//import utilities.math.language.mathboolean.node.Or;

public class GPRNormalization {

//	public Set<Set<String>> getVariablesToSencetree(
//			AbstractSyntaxTree<DataTypeEnum, IValue> geneRule, boolean sence) throws Exception {
//		
//		AbstractSyntaxTreeNode<DataTypeEnum, IValue> node = geneRule.getRootNode();
//		return getVariablesToSenceNode(node, sence);
//	}
//	
//	public Set<Set<String>> getVariablesToSenceNode(AbstractSyntaxTreeNode<DataTypeEnum, IValue> node, boolean sence) throws Exception{
//		
//		Set<Set<String>> ret = new HashSet<Set<String>>();
//		
//		if(node instanceof And){
//			if(node.getNumberOfChildren()>2)
//				throw new Exception("ERRO");
//			
//			Set<Set<String>> res1 = getVariablesToSenceNode(node.getChildAt(0), sence);
//			Set<Set<String>> res2 = getVariablesToSenceNode(node.getChildAt(1), sence);
//			
//			if(sence)
//				ret = propagate(res1, res2);
//			else
//				ret = aglumerate(res1, res2);
//						
//		}else if(node instanceof Or){
//			if(node.getNumberOfChildren()>2)
//				throw new Exception("ERRO");
//			
//			Set<Set<String>> res1 = getVariablesToSenceNode(node.getChildAt(0), sence);
//			Set<Set<String>> res2 = getVariablesToSenceNode(node.getChildAt(1), sence);
//			
//			if(sence)
//				ret = aglumerate(res1, res2);
//			else
//				ret = propagate(res1, res2);
//			
//			
////		}else if(node instanceof Not){
////			ret = getVariablesToSenceNode(node.getChildAt(0), !sence);
////		
//		}else if(node.isLeaf()){
//		
//			Set<String> set = new TreeSet<String>();
//			set.add(node.toString());
//			ret.add(set);
//		}
////		System.out.println(node.getClass().getSimpleName()+" " + ret);
//		return ret;
//	}
//	
//	
//	public Set<Set<String>> aglumerate(Set<Set<String>> res1,
//			Set<Set<String>> res2) {
//		
//		Set<Set<String>> ret = new HashSet<Set<String>>();
//		
//		ret.addAll(res1);
//		ret.addAll(res2);
//		
//		return ret;
//	}
//
//
//	public Set<Set<String>> propagate(Set<Set<String>> res1,
//			Set<Set<String>> res2) {
//		Set<Set<String>> ret = new HashSet<Set<String>>();
//		
//		
//		for(Set<String> r1 : res1)
//			for(Set<String> r2: res2)
//				ret.add(CollectionUtils.getReunionValues(r1, r2));
//		
//		return ret;
//	}
	
}
