/**
 * 
 */
package net.project.events;

import java.util.Hashtable;

/**
 *
 */
public enum EventType {

	NEW("new"),
	EDITED("edited"),
	DELETED("deleted"),
	COMMENTED("commented"),
	CHECKED_IN("checked_in"),
	CHECKED_OUT("checked_out"),
	UNDO_CHECKED_OUT("undo_checked_out"),
	FOLDER_CREATED("folder_created"),
	FOLDER_DELETED("folder_deleted"),
	MOVED("moved"),
	VIEWED("viewed"),
	FORM_RECORD_CREATED("form_record_created"),
	FORM_RECORD_MODIFIED("form_record_modified"),
	UNDO_REMOVE_CONTAINER("undo_remove_container"),
	UNDO_REMOVE_DOCUMENT("undo_remove_document"),
	IMAGE_UPLOADED("image_uploaded"),
	MEMBER_ADDED_TO_SPACE("member_added_to_space"),
	MEMBER_DELETED_FROM_SPACE("member_deleted_from_space"),
	OVERALL_STATUS_CHANGED("overall_status_changed");
	
	private final String text;

	private EventType(String value) {
		text = value;
	}

	private static Hashtable<String, EventType> supported = null;

	public static final EventType get(String type) {
		if (type == null)
			return null;

		if (supported == null) {
			supported = new Hashtable<String, EventType>();
			for (EventType b : EventType.values()) {
				supported.put(b.toString(), b);
				supported.put(b.text.toUpperCase(), b);
			}
		}
		return supported.get(type.toUpperCase());
	}

	public String getText() {
		return text;
	}

}
