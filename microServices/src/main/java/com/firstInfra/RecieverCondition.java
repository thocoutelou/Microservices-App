package com.firstInfra;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.context.annotation.Condition; 

public class RecieverCondition implements Condition {
	
	@Override 
	public boolean matches(ConditionContext context,AnnotatedTypeMetadata metadata) {
		return context.getBeanFactory().getBean("isReciever").equals(true);
	  }
}

