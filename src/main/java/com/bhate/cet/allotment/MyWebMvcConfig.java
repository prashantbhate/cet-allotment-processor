package com.bhate.cet.allotment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebMvcConfig {

	@Bean
	public WebMvcConfigurerAdapter forwardToIndex() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addViewController("/")
						.setViewName("redirect:swagger-ui.html");
			}
		};
	}

	//	@Bean
	//	public WebMethodHandlerMapping webMethodHandler() {
	//		return new WebMethodHandlerMapping();
	//	}

}

//class WebMethodHandlerMapping extends RequestMappingHandlerMapping {
//
//	@Override
//	protected boolean isHandler(Class<?> beanType) {
//		return (AnnotatedElementUtils.hasAnnotation(beanType, WebService.class) || AnnotatedElementUtils.hasAnnotation(beanType, WebMethod.class));
//	}
//
//	@Override
//	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
//		RequestMappingInfo info = createRequestMappingInfo(method);
//		if (info != null) {
//			RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
//			if (typeInfo != null) {
//				info = typeInfo.combine(info);
//			}
//		}
//		return info;
//	}
//
//	private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
//		WebMethod requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, WebMethod.class);
//		RequestCondition<?> condition = (element instanceof Class ? getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
//		return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
//	}
//
//	private RequestMappingInfo createRequestMappingInfo(WebMethod webMethod, RequestCondition<?> customCondition) {
//		return RequestMappingInfo.paths(webMethod.operationName())
//								 .methods(RequestMethod.POST)
//								 .mappingName(webMethod.operationName())
//								 .customCondition(customCondition)
//								 .build();
//	}
//}
