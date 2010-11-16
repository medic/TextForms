package net.frontlinesms.plugins.textforms.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;

/**
 * TextFormDao
 * @author dalezak
 *
 */
public interface TextFormDao {
	public List<TextForm> getAllTextForms();
	
	public List<TextForm> getAllTextForms(int startIndex, int limit);
	
	public void deleteTextForm(TextForm textform);
	
	public void saveTextForm(TextForm textform) throws DuplicateKeyException;
	
	public void updateTextForm(TextForm textform) throws DuplicateKeyException;
	
	public void saveTextFormWithoutDuplicateHandling(TextForm textform);
	
	public void updateTextFormWithoutDuplicateHandling(TextForm textform);
	
	public List<String> getKeywords();
	
	public TextForm getTextFormByKeyword(String keyword);
}