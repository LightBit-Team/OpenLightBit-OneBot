package am9.olbcore.onebot.feature.cave

import com.google.gson.annotations.Expose

class Comment(@Expose
             val sender: Long,
             @Expose
             val content: String
             ) {
}
