package com.rsicms.rsuite.utils.webService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.remoteapi.DefaultRemoteApiHandler;
import com.reallysi.rsuite.api.remoteapi.RemoteApiResult;
import com.reallysi.rsuite.api.remoteapi.result.MessageDialogResult;
import com.reallysi.rsuite.api.remoteapi.result.MessageType;
import com.reallysi.rsuite.api.remoteapi.result.NotificationAction;
import com.reallysi.rsuite.api.remoteapi.result.RestResult;
import com.reallysi.rsuite.api.remoteapi.result.UserInterfaceAction;
import com.rsicms.rsuite.helpers.messages.ProcessMessage;
import com.rsicms.rsuite.utils.conf.props.ConfPropsUtils;
import com.rsicms.rsuite.utils.operation.result.FileOperationResult;
import com.rsicms.rsuite.utils.operation.result.OperationResult;

/**
 * The intended base web service for this project's custom REST web services.
 */
public abstract class BaseWebService extends DefaultRemoteApiHandler implements WebServiceUtilsConstants {

	private static Log log = LogFactory.getLog(BaseWebService.class);

	/**
	 * Get the duration of notifications, in seconds.
	 * 
	 * @param context
	 * @return Duration of notifications, in seconds.
	 */
	protected static int getNotificationDurationInSeconds(ExecutionContext context) {
		return ConfPropsUtils.getPropertyAsInt(context.getConfigurationProperties(),
				PROP_NAME_NOTIFICATION_DURATION_IN_SECONDS, DEFAULT_NOTIFICATION_DURATION_IN_SECONDS);
	}

	/**
	 * Find out if less severe messages should be displayed alongside the more
	 * severe messages.
	 * 
	 * @param context
	 * @return True when less severe messages should be included; else, false.
	 */
	protected static boolean alsoDisplayLessSevereMessages(ExecutionContext context) {
		return ConfPropsUtils.getPropertyAsBoolean(context.getConfigurationProperties(),
				PROP_NAME_ALSO_DISPLAY_LESS_SEVERE_MESSAGES, DEFAULT_ALSO_DISPLAY_LESS_SEVERE_MESSAGES);
	}

	/**
	 * Translate the operation result into a remote API result. This one is
	 * ideal for <code>FileOperationResult</code> or when the subclass is
	 * overriding methods used by getWebServiceResponse() in order to create a
	 * different <code>RemoteApiResult</code> than one that incorporates the
	 * success* parameters, and when it doesn't want to refresh any objects in
	 * the CMS UI.
	 * 
	 * @param context
	 * @param user
	 * @param opResult
	 * @return remote API result.
	 */
	protected RemoteApiResult getWebServiceResponse(ExecutionContext context, User user, OperationResult opResult) {

		return getWebServiceResponse(context, user, opResult, new ArrayList<String>(), false, null, null);

	}

	/**
	 * Translate an operation result into a remote API result.
	 * <p>
	 * If there were failure or warnings, a message dialog box is presented, and
	 * no objects are refreshed in the display. Else, a notification action is
	 * included, and objects may be refreshed in the display.
	 * 
	 * @param context
	 * @param user
	 * @param opResult
	 * @param refreshUponSuccessIds
	 * @param refreshChildrenUponSuccess
	 * @param successUserInterfaceAction
	 * @param successMessage
	 * @return remote API result.
	 */
	protected RemoteApiResult getWebServiceResponse(ExecutionContext context, User user, OperationResult opResult,
			List<String> refreshUponSuccessIds, boolean refreshChildrenUponSuccess,
			UserInterfaceAction successUserInterfaceAction, String successMessage) {

		/*
		 * If the result is a file result and there were no errors, serve up the
		 * file.
		 */
		RemoteApiResult fileDownloadResult = null;
		if (opResult instanceof FileOperationResult) {
			fileDownloadResult = ((FileOperationResult) opResult).getFileForDownload();
		}

		StringBuilder buf = new StringBuilder();
		boolean includeLessSevereMessages = alsoDisplayLessSevereMessages(context);
		beginMessage(buf, opResult);

		if (opResult.hasFailures()) {

			addFailureMessages(buf, includeLessSevereMessages, opResult);
			closeMessage(buf, opResult);
			return getErrorResult(buf.toString());

		} else if (opResult.hasWarnings()) {

			if (fileDownloadResult != null) {

				// Warnings should already be in the log.
				return fileDownloadResult;

			} else {

				addWarningMessages(buf, includeLessSevereMessages, opResult);
				closeMessage(buf, opResult);
				return getWarningResult(buf.toString());

			}
		} else {

			if (fileDownloadResult != null) {

				return fileDownloadResult;

			} else {

				closeMessage(buf, opResult);
				RestResult restResult = getInfoResult(context, user, buf.toString(), refreshUponSuccessIds,
						refreshChildrenUponSuccess, successMessage);
				if (successUserInterfaceAction != null) {
					restResult.addAction(successUserInterfaceAction);
				}
				return restResult;
			}

		}
	}

	/**
	 * Adds the warning messages in the content message, conditionally, less
	 * severe messages.
	 * 
	 * @param buf
	 *            the content message.
	 * @param includeLessSevereMessages
	 *            Submit true to include info-level messages.
	 * @param opResult
	 *            the Operation Result.
	 */
	protected void addWarningMessages(StringBuilder buf, boolean includeLessSevereMessages, OperationResult opResult) {
		addMessages(buf, opResult.getWarningMessages(), "Warnings: ");
		if (includeLessSevereMessages) {
			addMessages(buf, opResult.getInfoMessages(), "Info: ");
		}
	}

	/**
	 * Adds the failure messages in the content message, conditionally, less
	 * severe messages.
	 * 
	 * @param buf
	 *            the content message.
	 * @param includeLessSevereMessages
	 *            Submit true to include warning- and info-level messages.
	 * @param opResult
	 *            the Operation Result.
	 */
	protected void addFailureMessages(StringBuilder buf, boolean includeLessSevereMessages, OperationResult opResult) {
		addMessages(buf, opResult.getFailureMessages(), "Errors: ");
		if (includeLessSevereMessages) {
			addMessages(buf, opResult.getWarningMessages(), "Warnings: ");
			addMessages(buf, opResult.getInfoMessages(), "Info: ");
		}
	}

	/**
	 * Adds a footer to the content message.
	 * 
	 * @param buf
	 *            the content message.
	 * @param opResult
	 *            the Operation Result.
	 */
	protected void closeMessage(StringBuilder buf, OperationResult opResult) {
	}

	/**
	 * Adds a header to the content message.
	 * 
	 * @param buf
	 *            the content message.
	 * @param opResult
	 *            the Operation Result.
	 */
	protected void beginMessage(StringBuilder buf, OperationResult opResult) {
	}

	/**
	 * Translate an operation result into a remote API result.
	 * <p>
	 * This signature doesn't enable the caller to control which objects are
	 * refreshed in the CMS UI.
	 * <p>
	 * If there were failure or warnings, a message dialog box is presented, and
	 * no objects are refreshed in the display. Else, a notification action is
	 * included, and objects may be refreshed in the display.
	 * 
	 * @param context
	 * @param user
	 * @param opResult
	 * @param successUserInterfaceAction
	 * @param successMessage
	 * @return remote API result.
	 */
	protected RemoteApiResult getWebServiceResponse(ExecutionContext context, User user, OperationResult opResult,
			UserInterfaceAction successUserInterfaceAction, String successMessage) {

		return getWebServiceResponse(context, user, opResult, new ArrayList<String>(), false,
				successUserInterfaceAction, successMessage);
	}

	/**
	 * Add list of messages to the string buffer, formatted with HTML.
	 * 
	 * @param buf
	 * @param messages
	 * @param messageType
	 */
	protected static void addMessages(StringBuilder buf, List<? extends ProcessMessage> messages, String messageType) {
		if (messages != null && messages.size() > 0) {
			buf.append("<p><b>" + messageType + "</b></p>");
			buf.append("<ul>");
			for (ProcessMessage message : messages) {
				buf.append("<li>")
						// Message can be truncated when too long; label
						// considered optional.
						// .append(message.getRelatedObjectLabel())
						// .append(": ")
						.append(message.getMessageText()).append("</li>");
			}
			buf.append("</ul>");
		}
	}

	/**
	 * Get the web service's label, which should be suitable for users.
	 * 
	 * @return The remoteApiDefinition element's label attribute value
	 */
	protected String getWebServiceLabel() {
		return getRemoteApiDefinition().getLabel();
	}

	/**
	 * Get an info remote API result.
	 * 
	 * @param context
	 * @param user
	 * @param msg
	 *            A message to include, incorporate, or ignore. It's up to the
	 *            implementation. Subclasses may override. If ignored, the
	 *            successMessage parameter is expected to be used. This
	 *            implementation ignores it, and creates a message using the
	 *            successMessage* parameters.
	 * @param refreshUponSuccessIds
	 * @param refreshChildrenUponSuccess
	 * @param successMessage
	 * @return A remote API result appropriate when there were no warnings or
	 *         failures.
	 */
	protected RestResult getInfoResult(ExecutionContext context, User user, String msg,
			List<String> refreshUponSuccessIds, boolean refreshChildrenUponSuccess, String successMessage) {

		log.info(new StringBuilder(user.getUserId()).append(": ").append(successMessage));

		RestResult webServiceResult = getNotificationResult(context, successMessage, getWebServiceLabel());

		// Add refresh action
		if (refreshUponSuccessIds != null && refreshUponSuccessIds.size() > 0) {
			UserInterfaceAction action = new UserInterfaceAction("rsuite:refreshManagedObjects");
			action.addProperty("objects", StringUtils.join(refreshUponSuccessIds, ","));
			action.addProperty("children", refreshChildrenUponSuccess);
			webServiceResult.addAction(action);
		}

		return webServiceResult;
	}

	/**
	 * Get a notification result, which honors a configurable duration and that
	 * the caller may add additional actions to.
	 * 
	 * @param context
	 * @param message
	 * @param title
	 * @return A <code>RestResult</code> that displays a notification in the CMS
	 *         UI.
	 */
	protected static RestResult getNotificationResult(ExecutionContext context, String message, String title) {
		RestResult webServiceResult = new RestResult();
		NotificationAction notification = new NotificationAction(message, title);
		notification.addProperty(NotificationAction.PROPERTY_DURATION, getNotificationDurationInSeconds(context));
		webServiceResult.addAction(notification);
		return webServiceResult;
	}

	/**
	 * Common implementation to display a warning to the user, that requires
	 * them to dismiss it.
	 * 
	 * @param msg
	 * @return message dialog result, of type error.
	 */
	protected RemoteApiResult getWarningResult(String msg) {
		return getWarningResult(msg, getWebServiceLabel());
	}

	/**
	 * Common implementation to display a warning to the user, that requires
	 * them to dismiss it.
	 * 
	 * @param msg
	 * @param title
	 * @return message dialog result, of type error.
	 */
	protected RemoteApiResult getWarningResult(String msg, String title) {
		return new MessageDialogResult(MessageType.WARNING, title, msg);
	}

	/**
	 * Common implementation to display an error to the user, that requires them
	 * to dismiss it.
	 * 
	 * @param msg
	 * @return message dialog result, of type error.
	 */
	protected RemoteApiResult getErrorResult(String msg) {
		return getErrorResult(msg, getWebServiceLabel());
	}

	/**
	 * Common implementation to display an error to the user, that requires them
	 * to dismiss it.
	 * 
	 * @param msg
	 * @param title
	 * @return message dialog result, of type error.
	 */
	protected RemoteApiResult getErrorResult(String msg, String title) {
		return new MessageDialogResult(MessageType.ERROR, title, msg);
	}

}
