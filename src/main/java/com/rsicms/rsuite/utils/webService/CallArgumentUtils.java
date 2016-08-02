package com.rsicms.rsuite.utils.webService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.reallysi.rsuite.api.remoteapi.CallArgument;
import com.reallysi.rsuite.api.remoteapi.CallArgumentList;

public class CallArgumentUtils {

  /**
   * Log the call arguments' names and values.
   * 
   * @param args
   */
  public static void logArguments(CallArgumentList args, Log log) {
    StringBuilder sb = new StringBuilder("Parameters:");
    for (CallArgument arg : args.getAll()) {
      sb.append("\n\t\"").append(arg.getName()).append("\": ");
      if (arg.isFileItem()) {
        sb.append("[FileItem]");
      } else if (arg.isFile()) {
        sb.append("[File]");
      } else {
        sb.append("\"").append(arg.getValue()).append("\"");
      }
    }
    log.info(sb);
  }

  /**
   * Get call arguments with names that begin with the specified prefix
   * 
   * @param args Call argument list to inspect.
   * @param prefix Prefix the call argument name must begin with, in order to qualify.
   * @param stripPrefix Submit true if the prefix is to be stripped from the names of returned call
   *        arguments.
   * @param trimValue Submit true if whitespace is to be trimmed from the value of qualifying call
   *        arguments.
   * @param keepBlankStrings Submit true if args whose values are the empty string should be
   *        retained
   * @return List of call arguments that qualify.
   */
  public static List<CallArgument> getArgumentsWithSameNamePrefix(CallArgumentList args,
      String prefix, boolean stripPrefix, boolean trimValue, boolean keepBlankStrings) {
    List<CallArgument> withPrefix = new ArrayList<CallArgument>();
    if (args != null) {
      for (CallArgument arg : args.getAll()) {
        if (arg.getName().startsWith(prefix)
            && (keepBlankStrings ? true : StringUtils.isNotBlank(arg.getValue()))) {
          if (stripPrefix || trimValue) {
            withPrefix.add(new CallArgument(
                stripPrefix ? arg.getName().substring(prefix.length()) : arg.getName(),
                trimValue ? arg.getValue().trim() : arg.getValue()));
          } else {
            withPrefix.add(arg);
          }
        }
      }
    }
    return withPrefix;
  }

}
