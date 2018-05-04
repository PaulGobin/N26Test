package com.n26.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/******************************************
 * This class auto configure swagger and also expose our rest API documentation to http://xxxxxx:port/swagger-ui.html.
 * 
 * The documentation headers such as the title, version etc are pulled from the distributed config service at start-up.
 * 
 * If none is defined, then the global default is used, if no global default is found, then the default defined in the place-holder below is used.
 * 
 * @author pgobin
 *
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final Logger log = LogManager.getLogger(SwaggerConfig.class);

	@Value("${api.app.title:N26 Statistic Service}")
	private String _title;

	@Value("${api.app.description:<b>This service records N26 financial transactions and provides statistics for the last 60 seconds.</b>"
		+ "<br/>You can test this service via the rest endpoints below by clicking/expanding the <b>statistics-controller </b> link below.<br/>"
		+ "There you will find the applicable post and get operations with detailed instructions of usage and functionality."
		+ "<br/>Every operation require an <b>x-account</b> and <b>x-authtoken</b> header, you can use any values since it's not being validated.}")
	private String _description;

	@Value("${api.app.version:1.0.0}")
	private String _version;

	@Value("${api.TermsAndCondition.URL:http://tandc.api.n26.com}")
	private String _termsAndCondition;

	@Value("${api.contact.email:PaulGobin@gmail.com}")
	private String _contactEmail;

	@Value("${api.license.info:All Rights Reserved. Paul Gobin}")
	private String _licenseInfo;

	@Value("${api.license.url:http://api.license.n26.com}")
	private String _licenseUrl;

	@Value("${api.organization.name:Paul Gobin for N26}")
	private String _organizationName;

	@Value("${spring.application.name}")
	private String applicationName;

	public SwaggerConfig()
	{

	}

	/**************************************************************************************
	 * Register a bean used to build swagger UI and inject our service endpoint documentation.
	 * 
	 * Additionally, this allow us to test our endpoint via swagger.
	 * 
	 * @return
	 **************************************************************************************/
	@Bean
	public Docket api()
	{
		log.info("Building Swagger documentation...");
		List<Parameter> headerParameters = getHeaderParameters();
		if (headerParameters.isEmpty() == false)
		{
			return new Docket(DocumentationType.SWAGGER_2).globalOperationParameters(headerParameters).groupName(applicationName).select()
				.apis(RequestHandlerSelectors.basePackage("com.n26.app.controller")).paths(PathSelectors.any()).build().apiInfo(apiInfo());
		} else
		{
			return new Docket(DocumentationType.SWAGGER_2).groupName(applicationName).select().apis(RequestHandlerSelectors.basePackage("com.n26.app.controller")).paths(PathSelectors.any()).build()
				.apiInfo(apiInfo());
		}
	}

	/**************************************************************************************
	 * Enforce the use of HTTP headers in every request, this is important for securing our HTTP endpoints.
	 * 
	 * Additionally, we can easily add an http interceptor and hook it into a oAuth2 service.
	 * 
	 * In a production environment, it's best to use an API Gateway and secure it with third<br>
	 * party tools like Mulesoft, APIgee etc, or our own oAuth server.
	 * 
	 * @return a list of parameters used for HTTP header
	 ***************************************************************************************/
	private List<Parameter> getHeaderParameters()
	{
		List<HeaderParams> headerParamsObj = new ArrayList<>();
		List<Parameter> paramBuilders = new ArrayList<>();
		headerParamsObj.add(new HeaderParams("x-account", "The account that was provided to you.", true));
		headerParamsObj.add(new HeaderParams("x-authtoken", "The authorization token received.", true));
		try
		{
			for (HeaderParams parameter : headerParamsObj)
			{
				String name = parameter.headerName;
				String desc = parameter.desc;
				boolean require = parameter.require;
				paramBuilders.add(new ParameterBuilder().name(name).description(desc).modelRef(new ModelRef("string")).parameterType("header").required(require).build());
			}
		} catch (Exception e)
		{
			log.error("Error in your swagger api.header.params.defs definition config", e);
		}
		return paramBuilders;
	}

	/**************************************************************************************
	 * Generate the Swagger HTML Header Documentation such as organization name, <br>
	 * URL to our licensing page, Terms and Condition etc.
	 * 
	 * @return
	 ***************************************************************************************/
	private ApiInfo apiInfo()
	{
		springfox.documentation.service.Contact c = new springfox.documentation.service.Contact(_organizationName, _licenseUrl, _contactEmail);
		return new ApiInfoBuilder().title(_title).description(_description).termsOfServiceUrl(_termsAndCondition).contact(c).license(_licenseInfo).licenseUrl(_licenseUrl).version(_version).build();
	}

	/**************************************************************************************
	 * A convenience class for building header attributes. We can easily feed <br>
	 * json data or read data from persistence storage.
	 * 
	 * This allows us to globally, or individually configure each microservice header. <br>
	 * By using the service name (spring.application.name) this can be automatically injected.
	 * 
	 * @author pgobin
	 *
	 ***************************************************************************************/
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class HeaderParams {
		public String headerName;
		public String desc;
		public boolean require = false;

		/**
		 * 
		 */
		public HeaderParams()
		{
			super();
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param headerName
		 * @param desc
		 * @param require
		 */
		public HeaderParams(String headerName, String desc, boolean require)
		{
			super();
			this.headerName = headerName;
			this.desc = desc;
			this.require = require;
		}

	}

}
