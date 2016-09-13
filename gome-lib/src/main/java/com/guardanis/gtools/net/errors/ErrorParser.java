package com.guardanis.gtools.net.errors;

import java.util.List;

import com.guardanis.gtools.net.WebResult;

public interface ErrorParser {

    public List<String> parseErrorMessages(WebResult result);

}