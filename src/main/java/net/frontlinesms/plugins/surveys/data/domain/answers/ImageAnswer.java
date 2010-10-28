package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.questions.ImageQuestion;

@Entity
public class ImageAnswer extends Answer<ImageQuestion> {
	
	public ImageAnswer() {
		super();
	}

	public ImageAnswer(FrontlineMessage message, Contact contact, Date dateSubmitted, String organizationId, ImageQuestion question) {
		super(message, contact, dateSubmitted, organizationId, question);
	}
	
	@Override
	public boolean isAnswerFor(Question question) {
		return question.getClass() == ImageQuestion.class;
	}
	
	@Override
	public String getAnswerValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return words[0].trim();
		}
		else if (words != null && words.length == 2) {
			return words[1].trim();
		}
		return null;
	}
	
	@Column(nullable=true)
	private byte[] imageBytes;

	/**
	 * Get Image
	 * @return BufferedImage
	 */
    public BufferedImage getImage() {
        try {
        	if (imageBytes != null && imageBytes.length > 0) {
        		InputStream in = new ByteArrayInputStream(imageBytes);
            	return ImageIO.read(in);
        	}
        } 
        catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

    /**
     * Set Image
     * @param image BufferedImage
     */
    public void setImage(BufferedImage image) {
    	try {
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		try {
    			ImageIO.write(image, "JPG", out);
    			imageBytes = out.toByteArray();	
    		}
    		finally {
    			out.close();
    		}
        } 
    	catch (IOException e) {
			e.printStackTrace();
		}
    }
}
