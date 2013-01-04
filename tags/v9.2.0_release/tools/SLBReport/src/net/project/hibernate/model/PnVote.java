package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnVote implements Serializable {

    /** identifier field */
    private BigDecimal voteId;

    /** identifier field */
    private String question;

    /** identifier field */
    private String response;

    /** identifier field */
    private String comments;

    /** full constructor */
    public PnVote(BigDecimal voteId, String question, String response, String comments) {
        this.voteId = voteId;
        this.question = question;
        this.response = response;
        this.comments = comments;
    }

    /** default constructor */
    public PnVote() {
    }

    public BigDecimal getVoteId() {
        return this.voteId;
    }

    public void setVoteId(BigDecimal voteId) {
        this.voteId = voteId;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("voteId", getVoteId())
            .append("question", getQuestion())
            .append("response", getResponse())
            .append("comments", getComments())
            .toString();
    }

}
