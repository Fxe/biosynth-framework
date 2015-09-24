package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.transaction.annotation.Transactional;

@Retention(RetentionPolicy.RUNTIME)
@Transactional(value="neo4jTransactionManager")
public @interface GraphTransactional {

}
