package net.frontlinesms.plugins.textforms.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.textforms.data.domain.TextFormResponse;

/**
 * TextFormResponseDao
 * @author dalezak
 *
 */
public interface TextFormResponseDao {
	public List<TextFormResponse> getAllTextFormResponses();
	
	public List<TextFormResponse> getAllTextFormResponses(int startIndex, int limit);
	
	public void deleteTextForm(TextFormResponse textformResponse);
	
	public void saveTextForm(TextFormResponse textformResponse) throws DuplicateKeyException;
	
	public void updateTextForm(TextFormResponse textformResponse) throws DuplicateKeyException;
	
	public TextFormResponse getTextFormResponseByContact(Contact contact);
}