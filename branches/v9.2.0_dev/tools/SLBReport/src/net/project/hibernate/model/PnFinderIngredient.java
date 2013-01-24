package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnFinderIngredient implements Serializable {

    /** identifier field */
    private BigDecimal ingredientsId;

    /** persistent field */
    private Clob ingredientsData;

    /** persistent field */
    private Set pnViews;

    /** full constructor */
    public PnFinderIngredient(BigDecimal ingredientsId, Clob ingredientsData, Set pnViews) {
        this.ingredientsId = ingredientsId;
        this.ingredientsData = ingredientsData;
        this.pnViews = pnViews;
    }

    /** default constructor */
    public PnFinderIngredient() {
    }

    public BigDecimal getIngredientsId() {
        return this.ingredientsId;
    }

    public void setIngredientsId(BigDecimal ingredientsId) {
        this.ingredientsId = ingredientsId;
    }

    public Clob getIngredientsData() {
        return this.ingredientsData;
    }

    public void setIngredientsData(Clob ingredientsData) {
        this.ingredientsData = ingredientsData;
    }

    public Set getPnViews() {
        return this.pnViews;
    }

    public void setPnViews(Set pnViews) {
        this.pnViews = pnViews;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ingredientsId", getIngredientsId())
            .toString();
    }

}
