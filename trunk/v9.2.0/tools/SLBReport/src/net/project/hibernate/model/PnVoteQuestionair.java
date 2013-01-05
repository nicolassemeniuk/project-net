package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnVoteQuestionair implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal voteId;

    /** identifier field */
    private String question;

    /** identifier field */
    private String title;

    /** full constructor */
    public PnVoteQuestionair(BigDecimal spaceId, BigDecimal voteId, String question, String title) {
        this.spaceId = spaceId;
        this.voteId = voteId;
        this.question = question;
        this.title = title;
    }

    /** default constructor */
    public PnVoteQuestionair() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("voteId", getVoteId())
            .append("question", getQuestion())
            .append("title", getTitle())
            .toString();
    }

}
