package com.firstInfra;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class PublisherCondition implements Condition{
	
	@Override 
	public boolean matches(ConditionContext context,AnnotatedTypeMetadata metadata) {
		return context.getBeanFactory().getBean("isReciever").equals(false);
	  }
}



