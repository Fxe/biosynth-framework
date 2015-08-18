package pt.uminho.sysbio.biosynthframework.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.Entity;

@Target(value = {TYPE})
@Retention(RUNTIME)
@Entity
public @interface BiosynthEntity {
	String majorLabel();
	String[] labels() default {};
}
