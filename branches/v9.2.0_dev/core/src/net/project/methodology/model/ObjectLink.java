/**
 * 
 */
package net.project.methodology.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author ljubisa
 * 
 */
public class ObjectLink implements Serializable {

	public ObjectLink() {
	}

	public ObjectLink(Integer fromObjectIdNew, Integer fromObjectIdOld, Integer toObjectIdOld, Integer context) {
		this.fromObjectIdNew = fromObjectIdNew;
		this.fromObjectIdOld = fromObjectIdOld;
		this.toObjectIdOld = toObjectIdOld;
		this.context = context;
	}

	private static final long serialVersionUID = 1L;

	private Integer fromObjectIdNew;

	private Integer fromObjectIdOld;

	private Integer toObjectIdNew;

	private Integer toObjectIdOld;

	private Integer context;
	
	private boolean saved = false;
	
	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public Integer getFromObjectIdNew() {
		return fromObjectIdNew;
	}

	public void setFromObjectIdNew(Integer fromObjectIdNew) {
		this.fromObjectIdNew = fromObjectIdNew;
	}

	public Integer getFromObjectIdOld() {
		return fromObjectIdOld;
	}

	public void setFromObjectIdOld(Integer fromObjectIdOld) {
		this.fromObjectIdOld = fromObjectIdOld;
	}

	public Integer getToObjectIdNew() {
		return toObjectIdNew;
	}

	public void setToObjectIdNew(Integer toObjectIdNew) {
		this.toObjectIdNew = toObjectIdNew;
	}

	public Integer getToObjectIdOld() {
		return toObjectIdOld;
	}

	public void setToObjectIdOld(Integer toObjectIdOld) {
		this.toObjectIdOld = toObjectIdOld;
	}

	public Integer getContext() {
		return context;
	}

	public void setContext(Integer context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("fromObjectIdOld", getFromObjectIdOld()).append("fromObjectIdNew", getFromObjectIdNew())
				.append("toObjectIdOld", getToObjectIdOld()).append("toObjectIdNew", getToObjectIdNew()).append("context", getContext()).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((fromObjectIdNew == null) ? 0 : fromObjectIdNew.hashCode());
		result = prime * result + ((fromObjectIdOld == null) ? 0 : fromObjectIdOld.hashCode());
		result = prime * result + ((toObjectIdNew == null) ? 0 : toObjectIdNew.hashCode());
		result = prime * result + ((toObjectIdOld == null) ? 0 : toObjectIdOld.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectLink other = (ObjectLink) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (fromObjectIdNew == null) {
			if (other.fromObjectIdNew != null)
				return false;
		} else if (!fromObjectIdNew.equals(other.fromObjectIdNew))
			return false;
		if (fromObjectIdOld == null) {
			if (other.fromObjectIdOld != null)
				return false;
		} else if (!fromObjectIdOld.equals(other.fromObjectIdOld))
			return false;
		if (toObjectIdNew == null) {
			if (other.toObjectIdNew != null)
				return false;
		} else if (!toObjectIdNew.equals(other.toObjectIdNew))
			return false;
		if (toObjectIdOld == null) {
			if (other.toObjectIdOld != null)
				return false;
		} else if (!toObjectIdOld.equals(other.toObjectIdOld))
			return false;
		return true;
	}

}
