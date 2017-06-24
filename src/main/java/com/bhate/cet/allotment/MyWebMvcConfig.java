package com.bhate.cet.allotment;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Context;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebMvcConfig {

	@Bean
	public FilterRegistrationBean someFilterRegistration() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(someFilter());
		registration.addUrlPatterns("/api/*");
		registration.addInitParameter("paramName", "paramValue");
		registration.setName("authFilter");
		registration.setOrder(1);
		return registration;
	}

	public Filter someFilter() {
		return new Filter() {

			@Override
			public void init(FilterConfig filterConfig) throws ServletException {

			}

			@Override
			public void doFilter(ServletRequest request,
								 ServletResponse response,
								 FilterChain chain) throws IOException, ServletException {
				if (request instanceof HttpServletRequest) {
					HttpServletRequest req = (HttpServletRequest) request;
					final String uri = req.getRequestURI();
					if (uri.endsWith("/login")) {
						invalidateAndCreateNew(req);
					} else {
						final HttpSession session = req.getSession(false);
						if (session == null) {
							throw new RuntimeException("Session does not exist");
						}
					}
				}
				chain.doFilter(request, response);
			}

			private void invalidateAndCreateNew(HttpServletRequest req) {
				final HttpSession session = req.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				req.getSession(true);
			}

			@Override
			public void destroy() {

			}
		};
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		factory.setTomcatContextCustomizers(Arrays.asList(new CustomCustomizer()));
		return factory;
	}

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

	static class CustomCustomizer implements TomcatContextCustomizer {
		@Override
		public void customize(Context context) {
			context.setUseHttpOnly(false);
		}
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
