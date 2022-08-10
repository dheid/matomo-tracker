package org.drjekyll.matomo.tracker;

import lombok.Value;

/**
 * A key-value pair that represents a custom information. See <a href="https://matomo.org/faq/how-to/guide-to-using-custom-variables-deprecated/">How do I use Custom Variables?</a>
 *
 * @deprecated Should not be used according to the Matomo FAQ: <a href="https://matomo.org/faq/how-to/guide-to-using-custom-variables-deprecated/">How do I use Custom Variables?</a>
 */
@Deprecated
@Value
public class CustomVariable {

  String key;

  String value;

}
