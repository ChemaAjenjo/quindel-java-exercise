package com.chemaajenjo.quindel.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.chemaajenjo.quindel.exception.QuindelDocumentConflictException;
import com.chemaajenjo.quindel.exception.QuindelDocumentNoContentException;
import com.chemaajenjo.quindel.exception.QuindelDocumentNotFoundException;
import com.chemaajenjo.quindel.model.QuindelDocument;
import com.chemaajenjo.quindel.service.QuindelService;

@RunWith(SpringJUnit4ClassRunner.class)
public class QuindelControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	private QuindelController controller;

	@Mock
	private QuindelService service;

	@InjectMocks
	private QuindelControllerAdvice controllerAdvice;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller, controllerAdvice).build();
	}

	@Test
	public void getDocumentContent_Test() throws Exception {

		when(service.getDocumentContent(Mockito.anyString())).thenReturn(populateDocument().getData());

		String id = "2000";

		mockMvc.perform(get("/quindel-api/document/" + id)).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$").value("line1\nline2\nline3\ngato\nline5"));

		verify(service, times(1)).getDocumentContent(id);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void getDocumentSize_Test() throws Exception {

		when(service.getDocumentSize(Mockito.anyString())).thenReturn(populateDocument().getData().split("\n").length);

		String id = "2000";

		mockMvc.perform(get("/quindel-api/document/" + id + "/lines")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$").value(5));

		verify(service, times(1)).getDocumentSize(id);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void getLine_Test() throws Exception {

		String id = "2000";
		Integer index = 1;

		when(service.getLine(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(populateDocument().getData().split("\n")[index - 1]);

		mockMvc.perform(get("/quindel-api/document/" + id + "/line/" + index)).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$").value("line1"));

		verify(service, times(1)).getLine(id, index);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void getLine_CONFLICT_Test() throws Exception {

		String id = "2000";
		Integer index = 0;

		when(service.getLine(Mockito.anyString(), Mockito.anyInt())).thenThrow(QuindelDocumentConflictException.class);

		mockMvc.perform(get("/quindel-api/document/" + id + "/line/" + index)).andDo(print())
				.andExpect(status().isConflict()).andExpect(jsonPath("$.status").value("CONFLICT"));

		verify(service, times(1)).getLine(id, index);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void getLine_INTERNAL_SERVER_ERROR_Test() throws Exception {

		String id = "2000";
		Integer index = 0;

		when(service.getLine(Mockito.anyString(), Mockito.anyInt())).thenThrow(Exception.class);

		mockMvc.perform(get("/quindel-api/document/" + id + "/line/" + index)).andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"));

		verify(service, times(1)).getLine(id, index);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void addLine_Test() throws Exception {

		String id = "2000";
		String data = "new final line";

		mockMvc.perform(put("/quindel-api/document/" + id + "/line").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{\"data\":\"" + data + "\"}")).andDo(print()).andExpect(status().isOk());

		verify(service, times(1)).addLine(id, data);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void addLine_NOT_FOUND_Test() throws Exception {

		String id = "2000";
		String data = "new final line";

		doThrow(QuindelDocumentNotFoundException.class).when(service).addLine(id, data);

		mockMvc.perform(put("/quindel-api/document/" + id + "/line").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{\"data\":\"" + data + "\"}")).andDo(print()).andExpect(status().isNotFound());

		verify(service, times(1)).addLine(id, data);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void updateLine_Test() throws Exception {

		String id = "2000";
		String data = "new line";
		Integer index = 2;

		QuindelDocument document = new QuindelDocument();
		document.setId(id);
		document.setData(data);
		document.setIndex(index);

		mockMvc.perform(put("/quindel-api/document/" + id + "/line/" + index)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content("{\"data\":\"" + data + "\"}")).andDo(print())
				.andExpect(status().isOk());

		verify(service, times(1)).updateLine(Mockito.any(QuindelDocument.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	public void addLine_MultipleDocuments_Test() throws Exception {

		String id1 = "2000";
		String data1 = "endfile1";
		String id2 = "3000";
		String data2 = "endfile2";

		String content = "[{\"id\": \"" + id1 + "\",\"data\": \"" + data1 + "\"},{\"id\": \"" + id2 + "\",\"data\": \""
				+ data2 + "\"}]";
		mockMvc.perform(put("/quindel-api/document/line?type=add").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)).andDo(print()).andExpect(status().isOk());

		verify(service, times(1)).addLine(id1, data1);
		verify(service, times(1)).addLine(id2, data2);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void updateLine_MultipleDocuments_Test() throws Exception {

		String id1 = "2000";
		String data1 = "endfile1";
		Integer index1 = 2;
		String id2 = "3000";
		String data2 = "endfile2";
		Integer index2 = 1;

		String content = "[{\"id\": \"" + id1 + "\",\"data\": \"" + data1 + "\", \"index\" : \"" + index1
				+ "\" },{\"id\": \"" + id2 + "\",\"data\": \"" + data2 + "\", \"index\" : \"" + index2 + "\" }]";

		mockMvc.perform(put("/quindel-api/document/line?type=update").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)).andDo(print()).andExpect(status().isOk());

		verify(service, times(2)).updateLine(Mockito.any(QuindelDocument.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	public void findLineByWord_Test() throws Exception {

		String id = "2000";
		String query = "gato";

		when(service.findLineByWord(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(populateDocument()));

		mockMvc.perform(get("/quindel-api/document/" + id + "/search?query=" + query)).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("2000"));

		verify(service, times(1)).findLineByWord(id, query);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void findLineByWord_MultipleDocument_Test() throws Exception {

		String query = "gato";

		when(service.findLineByWord(Mockito.anyString())).thenReturn(Arrays.asList(populateDocument()));
		when(service.findLineByWord(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(populateDocument()));

		mockMvc.perform(get("/quindel-api/document/search?query=" + query)).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value("2000"));

		verify(service, times(1)).findLineByWord(query);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void findLineByWord_NOT_CONTENT_Test() throws Exception {

		String id = "2000";
		String query = "gato";

		when(service.findLineByWord(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(QuindelDocumentNoContentException.class);

		mockMvc.perform(get("/quindel-api/document/" + id + "/search?query=" + query)).andDo(print())
				.andExpect(status().isNoContent());

		verify(service, times(1)).findLineByWord(id, query);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void saveDocument_Test() throws Exception {

		mockMvc.perform(post("/quindel-api/document/save").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{\"id\":\"3000\",\"data\":\"line3\\nline2\"}")).andDo(print())
				.andExpect(status().isCreated());

		verify(service, times(1)).storageDocument(Mockito.any(QuindelDocument.class));
		verifyNoMoreInteractions(service);
	}

	private QuindelDocument populateDocument() {

		QuindelDocument document = new QuindelDocument();
		document.setId("2000");
		document.setData("line1\nline2\nline3\ngato\nline5");

		return document;
	}

}
