package com.lzp.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val lyric = "你发如雪\n" +
            "凄美了离别\n" +
            "我焚香感动了谁\n" +
            "邀明月让回忆皎洁\n" +
            "爱在月光下完美\n" +
            "你发如雪\n" +
            "纷飞了眼泪\n" +
            "我等待苍老了谁\n" +
            "红尘醉微醺的岁月\n" +
            "我用无悔\n" +
            "刻永世爱你的碑"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 设置歌词
        text.text = lyric

        start.setOnClickListener {
            val lyricList = lyric.split("\n")
            var startIndex = 0
            var delayTime = 0L
//            text.startPlayLine(
//                0,
//                lyric.length,
//                5000
//            )
            lyricList.forEach {
                val startIndexTemp = startIndex
                val duration = (500 + Math.random() * 500).toLong()
                text.postDelayed(
                    {
                        text.startPlayLine(
                            startIndexTemp,
                            min(lyric.length, startIndexTemp + it.length + 1), // 因为有个换行符，所以 + 1
                            duration
                        )
                    },
                    delayTime
                )
                delayTime += duration + 50
                startIndex += it.length + 1
            }
        }

        end.setOnClickListener {
            text.stopPlay()
        }


    }
}
