package com.rsicms.rsuite.utils.webService;

import org.apache.commons.logging.Log;

import com.reallysi.rsuite.api.remoteapi.CallArgument;
import com.reallysi.rsuite.api.remoteapi.CallArgumentList;

public class CallArgumentUtils {

  /**
   * Log the call arguments' names and values.
   * 
   * @param args
   */
  public static void logArguments(
      CallArgumentList args,
      Log log) {
    StringBuilder sb = new StringBuilder("Parameters:");
    for (CallArgument arg : args.getAll()) {
      sb.append(
          "\n\t\"").append(
          arg.getName()).append(
          "\": ");
      if (arg.isFileItem()) {
        sb.append("[FileItem]");
      } else if (arg.isFile()) {
        sb.append("[File]");
      } else {
        sb.append(
            "\"").append(
            arg.getValue()).append(
            "\"");
      }
    }
    log.info(sb);
  }
}
