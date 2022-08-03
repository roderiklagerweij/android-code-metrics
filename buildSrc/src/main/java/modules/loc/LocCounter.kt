package modules.loc

class LocCounter {

    fun count(lines : List<String>): Int {
        var count = 0
        var commentBegan = false
        for (processLine in lines) {

            var line = processLine.trim { it <= ' ' }
            if ("" == line || line.startsWith("//")) {
                continue
            }
            if (commentBegan) {
                if (commentEnded(line)) {
                    line = line.substring(line.indexOf("*/") + 2).trim { it <= ' ' }
                    commentBegan = false
                    if ("" == line || line.startsWith("//")) {
                        continue
                    }
                } else continue
            }
            if (isSourceCodeLine(line)) {
                count++
            }
            if (commentBegan(line)) {
                commentBegan = true
            }
        }
        return count
    }

    /**
     *
     * @param line
     * @return This method checks if in the given line a comment has begun and has not ended
     */
    private fun commentBegan(line: String): Boolean {
        // If line = /* */, this method will return false
        // If line = /* */ /*, this method will return true
        var line = line
        val index = line.indexOf("/*")
        if (index < 0) {
            return false
        }
        var quoteStartIndex = line.indexOf("\"")
        if (quoteStartIndex != -1 && quoteStartIndex < index) {
            while (quoteStartIndex > -1) {
                line = line.substring(quoteStartIndex + 1)
                val quoteEndIndex = line.indexOf("\"")
                line = line.substring(quoteEndIndex + 1)
                quoteStartIndex = line.indexOf("\"")
            }
            return commentBegan(line)
        }
        return !commentEnded(line.substring(index + 2))
    }

    /**
     *
     * @param line
     * @return This method checks if in the given line a comment has ended and no new comment has not begun
     */
    private fun commentEnded(line: String): Boolean {
        // If line = */ /* , this method will return false
        // If line = */ /* */, this method will return true
        val index = line.indexOf("*/")
        return if (index < 0) {
            false
        } else {
            val subString = line.substring(index + 2).trim { it <= ' ' }
            if ("" == subString || subString.startsWith("//")) {
                return true
            }
            !commentBegan(subString)
        }
    }

    /**
     *
     * @param line
     * @return This method returns true if there is any valid source code in the given input line. It does not worry if comment has begun or not.
     * This method will work only if we are sure that comment has not already begun previously. Hence, this method should be called only after [.commentBegan] is called
     */
    private fun isSourceCodeLine(line: String): Boolean {
        var line = line
        val isSourceCodeLine = false
        line = line.trim { it <= ' ' }
        if ("" == line || line.startsWith("//")) {
            return isSourceCodeLine
        }
        if (line.length == 1) {
            return true
        }
        val index = line.indexOf("/*")
        if (index != 0) {
            return true
        } else {
            while (line.length > 0) {
                line = line.substring(index + 2)
                val endCommentPosition = line.indexOf("*/")
                if (endCommentPosition < 0) {
                    return false
                }
                return if (endCommentPosition == line.length - 2) {
                    false
                } else {
                    val subString = line.substring(endCommentPosition + 2)
                        .trim { it <= ' ' }
                    if ("" == subString || subString.indexOf("//") == 0) {
                        false
                    } else {
                        if (subString.startsWith("/*")) {
                            line = subString
                            continue
                        }
                        true
                    }
                }
            }
        }
        return isSourceCodeLine
    }
}
