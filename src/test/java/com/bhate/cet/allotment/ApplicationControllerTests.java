/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bhate.cet.allotment;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isOneOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void itShouldReturnDefaultLimit() throws Exception {

		do_GET_allotments().andExpect(jsonPath("$.allotments").value(hasSize(15)));
	}

	@Test
	public void itShouldFilterSubstringByDefault() throws Exception {
		final String[] params = {"quota", "GMK", "branch", "CS", "college", "E", "lowCutoffRank", "0", "highCutoffRank", "8000"};
		do_GET_allotments(params).andExpect(jsonPath("$.allotments").value(hasSize(13)));
	}

	@Test
	public void itShouldFilterByRegex() throws Exception {
		final String[] params = {"branch", "CS|EC", "useRegexFilter", "true"};
		final Matcher<Iterable<String>> matcher = everyItem(isOneOf(" EC Electronics", " CS Computers"));
		do_GET_allotments(params).andExpect(jsonPath("$.allotments..branchName").value(matcher));
	}

	private ResultActions do_GET_allotments(String... paramValues) throws Exception {
		login();

		MockHttpSession session = new MockHttpSession();
		listFiles(session);

		selectFile(session);


		final MockHttpServletRequestBuilder requestBuilder = get("/api/v1.2/allotments").session(session);
		ParamBuilder.params(requestBuilder, paramValues);
		return this.mockMvc.perform(requestBuilder)
						   .andDo(print())
						   .andExpect(status().isOk());
	}

	private void selectFile(MockHttpSession session) throws Exception {
		this.mockMvc.perform(post("/api/v1.2/cutoff/files/current").param("cutoffFile", "engg_cutoff_2016.pdf")
																   .session(session))
					.andDo(print())
					.andExpect(status().isCreated());

		this.mockMvc.perform(get("/api/v1.2/cutoff/files/current").session(session))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.fileName").value("engg_cutoff_2016.pdf"));
	}

	private void listFiles(MockHttpSession session) throws Exception {
		this.mockMvc.perform(get("/api/v1.2/cutoff/files").param("cutoffFile", "")
														  .session(session))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$").value(hasItems("engg_cutoff_2016.pdf", "engg_cutoff_mock_2017.pdf")));
	}

	private void login() throws Exception {
		this.mockMvc.perform(get("/api/v1.2/login"))
					.andDo(print())
					.andExpect(status().isNoContent());
	}

	private static class ParamBuilder {

		private MockHttpServletRequestBuilder requestBuilder;
		private int i;
		private String key;
		private String value;

		public ParamBuilder(MockHttpServletRequestBuilder requestBuilder) {
			this.requestBuilder = requestBuilder;
		}

		private static void params(MockHttpServletRequestBuilder requestBuilder,
								   String[] paramValue) {
			final ParamBuilder paramBuilder = new ParamBuilder(requestBuilder);
			for (String s : paramValue) {
				paramBuilder.invoke(s);
			}
		}

		public void invoke(String s) {
			if (i % 2 == 0) {
				key = s;
			} else {
				value = s;
			}
			if (key != null && value != null) {
				requestBuilder.param(key, value);
				key = null;
				value = null;
			}
			i++;
		}
	}
}
