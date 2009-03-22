package org.joe_e.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

/**
 * This annotation is used to mark fields in a session view as
 * read only. The servlet will be able to modify them, but
 * these modifications will not be copied back to the actual
 * HttpSession. As a result, any changes to the field will
 * not actually be seen in the HttpSession.
 * as an example:
 * <code>
 * public SessionView extends AbstractSessionView {
 *     @readonly public String username;
 * }
 * </code>
 * Now in the servlet that defines this class, access to the SessionView.username
 * field is not restricted in any way, but when copying changes from the SessionView
 * back to the session, the username field will not get copied. 
 * 
 * However, this annotation alone does not protect against deep modifications
 * to the object. In order to do this, the field must also be cloneable in 
 * a meaningful way. It must implement the cloneable interface and making
 * a clone of the object must essentially create a deep copy of the object.
 */
public @interface readonly {
}
