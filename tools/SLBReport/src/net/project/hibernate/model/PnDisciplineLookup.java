package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDisciplineLookup implements Serializable {

    /** identifier field */
    private Integer disciplineCode;

    /** persistent field */
    private String disciplineName;

    /** nullable persistent field */
    private String disciplineDescription;

    /** persistent field */
    private Set pnPersonHasDisciplines;

    /** full constructor */
    public PnDisciplineLookup(Integer disciplineCode, String disciplineName, String disciplineDescription, Set pnPersonHasDisciplines) {
        this.disciplineCode = disciplineCode;
        this.disciplineName = disciplineName;
        this.disciplineDescription = disciplineDescription;
        this.pnPersonHasDisciplines = pnPersonHasDisciplines;
    }

    /** default constructor */
    public PnDisciplineLookup() {
    }

    /** minimal constructor */
    public PnDisciplineLookup(Integer disciplineCode, String disciplineName, Set pnPersonHasDisciplines) {
        this.disciplineCode = disciplineCode;
        this.disciplineName = disciplineName;
        this.pnPersonHasDisciplines = pnPersonHasDisciplines;
    }

    public Integer getDisciplineCode() {
        return this.disciplineCode;
    }

    public void setDisciplineCode(Integer disciplineCode) {
        this.disciplineCode = disciplineCode;
    }

    public String getDisciplineName() {
        return this.disciplineName;
    }

    public void setDisciplineName(String disciplineName) {
        this.disciplineName = disciplineName;
    }

    public String getDisciplineDescription() {
        return this.disciplineDescription;
    }

    public void setDisciplineDescription(String disciplineDescription) {
        this.disciplineDescription = disciplineDescription;
    }

    public Set getPnPersonHasDisciplines() {
        return this.pnPersonHasDisciplines;
    }

    public void setPnPersonHasDisciplines(Set pnPersonHasDisciplines) {
        this.pnPersonHasDisciplines = pnPersonHasDisciplines;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("disciplineCode", getDisciplineCode())
            .toString();
    }

}
