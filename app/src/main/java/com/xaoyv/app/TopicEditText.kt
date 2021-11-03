package com.ninexiu.video.view

import android.content.Context
import android.text.*
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ninexiu.video.R
import com.ninexiu.video.bean.Topic
import com.ninexiu.video.common.util.NSLog
import com.ninexiu.video.common.util.Utils
import java.util.*

/**
 * <pre>
 *     @author : bing
 *     time   : 2021/7/16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class TopicEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatEditText(context, attrs, defStyleAttr) {
    val TAG = "TopicEditText"

    //已添加的所有topic
    private val addedTopics = StringBuilder()

    //已添加的推荐topic
    private val addedRecommendTopics = ArrayList<Topic>()

    private val lastLength = intArrayOf(0)

    var topicCount = 0
    var currentTopicStr = ""

    var isEditTopicing = false
    private var isBuildTopic = false

    /**
     * 是否正在编辑话题的回调
     */
    var isEditTopicCallBack: ((isEditTopic: Boolean) -> Unit)? = null

//    /**
//     * 添加话题的回调  输入#
//     */
//    var onAddTopicCallBack: (() -> Unit)? = null

    /**
     * 正在编辑的话题
     */
    var onEditTopicChange: ((topicStr: String) -> Unit)? = null

    init {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                post {
                    if (!TextUtils.isEmpty(s)) {
                        val lastIndex = s.lastIndexOf("#")
                        if (!isEmojiBack(lastIndex, s)) {
                            val isEditTopic = isEditTopic(s, lastIndex)
                            val editTopicStr = getEditTopicStr(s, isEditTopic, lastIndex)
                            if (!TextUtils.equals(currentTopicStr, editTopicStr)) {
                                onEditTopicChange?.invoke(editTopicStr)
                            }
                            currentTopicStr = editTopicStr

                            if (s.length != lastLength[0] //不是buildEditTextColor设置
                                    && (s.endsWith(" ")
                                            || isEditTopic != isEditTopicing//编辑话题状态改变的时候
                                            || (isEditTopic && s.indexOf(" ", lastIndex) >= 0)//正在编辑话题，并且输入的内容中有空格
                                            || (isEditTopic && s.endsWith("#")))) {
                                isBuildTopic = true
                                text = buildEditTextColor(s)
                                text?.let {
                                    setSelection(it.length)
                                }
                            }
                            if (isEditTopic != isEditTopicing) {
                                isEditTopicing = isEditTopic
                                isEditTopicCallBack?.invoke(isEditTopicing)
                            }
                        } else {
                            if (isEditTopicing) {
                                isEditTopicing = false
                                isEditTopicCallBack?.invoke(isEditTopicing)
                            }
                        }
                    } else if (TextUtils.isEmpty(s)) {
                        isEditTopicing = false
                        NSLog.e(TAG, "没有在编辑2222222")
                        isEditTopicCallBack?.invoke(false)
                    }
                    lastLength[0] = s.length
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    /**
     * 添加推荐的topic
     */
    fun addRecommendTopic(topic: Topic) {
        if (topic.title == null || TextUtils.isEmpty(topic.title)) {
            return
        }

        if (topicCount >= 3 || (topicCount == 2 && !TextUtils.isEmpty(currentTopicStr))) {
            Toast.makeText(context, "最多添加3个话题哦", Toast.LENGTH_SHORT).show()
            return
        }

        formatRecommendTopic()

        if (addedRecommendTopics.contains(topic) || addedTopics.contains(topic.title!!)) {
            Toast.makeText(context, "话题已选择", Toast.LENGTH_SHORT).show()
            return
        }

        val temp = topic.title?.replace("#", "")

        text?.let {
            if (it.endsWith("#")) {
                it.append("$temp")
            } else {
                if (!TextUtils.isEmpty(currentTopicStr)) {
                    //正在编辑话题,先拼一个空格
                    it.append(" #${temp}")
                } else {
                    it.append("#${temp}")
                }
            }

            it.append(" ")
        }
        addedRecommendTopics.add(topic)
    }

    fun clearTopics() {
        addedRecommendTopics.clear()
        addedTopics.delete(0, addedTopics.length)
    }

    /**
     * 新建一个话题
     */
    fun addNewTopic() {
        if (topicCount >= 3) {
            Toast.makeText(context, "最多添加3个话题哦", Toast.LENGTH_SHORT).show()
            return
        }
        text?.let {
            if (!it.endsWith("#")) {
                it.append("#")
                Utils.setKeyboardUp(this)
            } else {
                Utils.setKeyboardUp(this)
            }
        }

    }

    fun formatRecommendTopic() {
        val iter = addedRecommendTopics.iterator()
        while (iter.hasNext()) {
            val recommendTopic = iter.next()
            if (text != null && !recommendTopic.title.isNullOrEmpty() && !text!!.contains(recommendTopic.title!!)) {
                iter.remove()
            }
        }
    }


    fun formatAddedTopicStr(): String? {
        text?.let {
            text = buildEditTextColor(it, true)
        }
        if (TextUtils.isEmpty(addedTopics)) {
            return ""
        }

        val topicStr = addedTopics.toString()
        for ((id, _, _, title) in addedRecommendTopics) {
            topicStr.replace(title!!, id.toString())
        }
        return topicStr
    }

    /**
     * 校验话题重复
     */
    fun checkTopicSame(): Boolean {
        var haveCount = 0
        for (recommendTopic in addedRecommendTopics) {
            if (recommendTopic.title != null && TextUtils.equals(currentTopicStr, "#${recommendTopic.title}")) {
                haveCount++
            }
        }
        val spiltTopics = addedTopics.split("#")
        for (addTopic in spiltTopics) {
            if (TextUtils.equals(currentTopicStr, "#${addTopic}")) {
                haveCount++
            }
        }
        return haveCount > 1
    }


    /**
     * 是否正在编辑话题
     */
    fun isEditTopic(s: Editable, lastIndex: Int): Boolean {
        if (lastIndex == s.length - 1) {
            return true
        }
        if (lastIndex >= 0) {
            return !s.substring(lastIndex).contains(" ")
        }
        return false
    }

    fun isEditTopic(): Boolean {
        return !TextUtils.isEmpty(currentTopicStr) || (text != null && text!!.endsWith("#"))
    }

    fun isEditTopicWithIndex(index: Int, dend: Int, source: CharSequence): Boolean {
        editableText?.let {
            val spans = it.getSpans(0, it.length, MForegroundColorSpan::class.java)
            for (span in spans) {
                if (index in it.getSpanStart(span) + 1..it.getSpanEnd(span)) {
                    if (span.spanSourceString.toByteArray().size + source.toString().toByteArray().size > 37) {
                        return true
                    } else {
                        if (TextUtils.isEmpty(source)) {//删除内容
                            span.spanSourceString = span.spanSourceString.substring(0, span.spanSourceString.length - dend + index)
                        } else {
                            span.spanSourceString = span.spanSourceString.plus(source)
                        }

                    }
                }
            }
        }
        return false
    }

    /**
     * 是否正在编辑话题
     */
    fun getEditTopicStr(s: Editable, isEditTopic: Boolean, lastIndex: Int): String {
        return if (isEditTopic && !s.endsWith("#") && lastIndex >= 0) {
            s.substring(s.lastIndexOf("#") + 1)
        } else {
            ""
        }
    }

    /**
     * 校验是不是emoji  从前往后
     */
    fun isEmojiFront(lastIndex: Int, content: Editable): Triple<Boolean, Int, Int> {
        val nextIndexTemp3 = content.indexOf("[", Math.max(0, lastIndex - 1))
        val nextIndexTemp4 = content.indexOf("]", lastIndex)
        //表情过滤掉不处理
        if (nextIndexTemp3 >= 0 && nextIndexTemp4 > 0 && nextIndexTemp3 == lastIndex - 1 && nextIndexTemp4 - nextIndexTemp3 < 15 && content.substring(nextIndexTemp3, nextIndexTemp4).contains("#img")) {
            return Triple(true, nextIndexTemp3, nextIndexTemp4)
        }
        return Triple(false, nextIndexTemp3, nextIndexTemp4)
    }

    /**
     * 校验是不是emoji  从后向前
     */
    fun isEmojiBack(lastIndex: Int, content: Editable): Boolean {
        if (lastIndex < 1) {
            return false
        }
        val nextIndexTemp3 = content.lastIndexOf("[", lastIndex)
        val nextIndexTemp4 = content.lastIndexOf("]", Math.min(content.length, lastIndex + 1))
        //表情过滤掉不处理
        if (nextIndexTemp3 >= 0 && nextIndexTemp4 > 0 && nextIndexTemp4 == lastIndex + 1 && nextIndexTemp4 - nextIndexTemp3 < 15 && content.substring(nextIndexTemp3, nextIndexTemp4).contains("#img")) {
            return true
        }
        return false
    }


    /**
     * 设置输入内容 话题颜色
     * @param content
     * @return
     */
    private fun buildEditTextColor(editable: Editable?, isPublish: Boolean = false): SpannableStringBuilder? {
        var builder = SpannableStringBuilder()
        editable?.let { content ->
            NSLog.e(TAG, "构建话题")
            topicCount = 0
            content.clearSpans()
            builder.append(content)
            var lastIndex = 0
            addedTopics.delete(0, addedTopics.length)
            while (content.indexOf("#", lastIndex) >= 0) {
                lastIndex = content.indexOf("#", lastIndex)
                var nextIndex = 0
                val nextIndexTemp1 = content.indexOf("#", lastIndex + 1)
                val nextIndexTemp2 = content.indexOf(" ", lastIndex)
                val triple = isEmojiFront(lastIndex, content)
                if (triple.first) {
                    NSLog.e("Topic 是个表情${triple.second}===========${triple.third}")
                    lastIndex = triple.third
                    continue
                }

                //下个空格在#后边，前边这个不算话题，直接跳入下个循环
                if (nextIndexTemp1 > 0 && (nextIndexTemp2 > nextIndexTemp1 || nextIndexTemp2 < 0)) {
//                lastIndex = nextIndexTemp1
//                continue
                    nextIndex = nextIndexTemp1
                }


                //前两种情况有大于0
                if (nextIndexTemp1 > 0 && nextIndexTemp2 > 0) {
                    if (lastIndex == nextIndexTemp2 - 1) {
                        //输入# 直接输入空格
                        lastIndex = nextIndexTemp2
                        continue
                    }
                    nextIndex = Math.min(nextIndexTemp1, nextIndexTemp2)
                } else if (nextIndexTemp1 > 0) {
                    nextIndex = nextIndexTemp1
                } else if (nextIndexTemp2 > 0) {
                    if (lastIndex == nextIndexTemp2 - 1) {
                        //输入# 直接输入空格
                        lastIndex = nextIndexTemp2
                        continue
                    }
                    nextIndex = nextIndexTemp2
                }
                //处理第三种情况
                if (nextIndex > 0 && triple.second > 0) {
                    nextIndex = Math.min(nextIndex, triple.second)
                } else if (triple.second > 0) {
                    nextIndex = triple.second
                }
                //三种情况都不大于0
                if (nextIndex == 0 && isPublish && topicCount < 3) {
//                nextIndex = Math.min(lastIndex + 13, content.length)
                    nextIndex = content.length
                }

                if (nextIndex > lastIndex) {
                    val spanStr = content.substring(lastIndex, nextIndex)
                    builder.setSpan(MForegroundColorSpan(ContextCompat.getColor(context, R.color.color_topic), spanStr), lastIndex, nextIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    addedTopics.append(spanStr)
                    NSLog.e(TAG, "添加的话题$addedTopics")
                    topicCount++
                } else {
                    break
                }
                lastIndex = nextIndex
            }
        }

        return builder
    }


//    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
//        return ZanyInputConnection(super.onCreateInputConnection(outAttrs), true, this)
//    }
//
//    inner class ZanyInputConnection(target: InputConnection?, mutable: Boolean, val editView: EditText) : InputConnectionWrapper(target, mutable) {
//        override fun sendKeyEvent(event: KeyEvent): Boolean {
//            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
//                NSLog.d("InputEdit : ","总长度 : ${editView.editableText.toString().length}  ,光标位置 : ${editView.selectionStart}")
//                if (dealEditText()) {
//                    NSLog.d("InputEdit : ", "interrupt")
//                    return true
//                }
//                return super.sendKeyEvent(event)
//            }
//            return super.sendKeyEvent(event)
//        }
//
//        private fun dealEditText(): Boolean {
//            val start = editView.selectionStart     // 光标位置
//
//            if(start == 0) {
//                return true;
//            }
//
//            val spans: Array<VerticalImageSpan>? = editView.editableText.getSpans(start - 1, start, VerticalImageSpan::class.java)
//
//            if (spans == null || spans.isEmpty()) {
//                editView.editableText.delete(editView.selectionStart - 1, editView.selectionStart)
//                return true
//            }
//
//            NSLog.d("InputEdit : ", "spanSize : ${spans.size}")
//
//            for (span in spans) {
//                val source = span.spanSource
//                NSLog.d("InputEdit : ", "source : $source , length : ${source.length} , start : ${start - source.length} , end : $start")
//                editView.editableText.removeSpan(span)
//                editView.editableText.delete(Math.max(0, start - source.length), start)
//                return true
//            }
//            return false
//        }
//
//
//        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
//            if (beforeLength == 1 && afterLength == 0) {
//                return sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
//            }
//            return super.deleteSurroundingText(beforeLength, afterLength)
//        }
//    }
}