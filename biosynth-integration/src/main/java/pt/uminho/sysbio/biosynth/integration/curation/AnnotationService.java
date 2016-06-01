package pt.uminho.sysbio.biosynth.integration.curation;

import pt.uminho.sysbio.biosynth.integration.AnnotationType;

public interface AnnotationService {

  public Long addAnnotation(AnnotationType type, long src, long dst, String user);

  public Long deleteAnnotation(long src, long dst);
}
