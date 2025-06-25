package com.bcs.breb.runners;

import com.bcs.breb.utils.customrunner.CustomRunner;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(CustomRunner.class)
@CucumberOptions(
    features = {"src/test/resources/features/generate_token.feature"},
    glue = {"com.bcs.breb.stepdefinitions", "com.bcs.breb.hooks"},
    monochrome = true, snippets = CucumberOptions.SnippetType.CAMELCASE,
    tags = ""
)
public class brebRunner {}
