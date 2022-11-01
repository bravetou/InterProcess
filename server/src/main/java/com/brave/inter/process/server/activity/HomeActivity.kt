package com.brave.inter.process.server.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.brave.inter.process.server.BR
import com.brave.inter.process.server.R
import com.brave.inter.process.server.adapter.FunctionAdapter
import com.brave.inter.process.server.bean.FunctionBean
import com.brave.inter.process.server.databinding.ActivityHomeBinding
import com.brave.inter.process.server.viewmodel.HomeViewModel
import com.brave.mvvmrapid.core.common.CommonActivity
import com.brave.mvvmrapid.utils.drawBackground

class HomeActivity : CommonActivity<ActivityHomeBinding, HomeViewModel>() {
    override val variableId: Int
        get() = BR.viewModel

    private val adapter by lazy {
        FunctionAdapter()
    }

    private val data by lazy {
        mutableListOf(
            FunctionBean(0, R.mipmap.icon_dot, "添加"),
            FunctionBean(1, R.mipmap.icon_dot, "根据姓名模糊查询"),
            FunctionBean(2, R.mipmap.icon_dot, "查询全部")
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setNewInstance(data)

        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            when (item.id) {
                0 -> {
                    // 添加
                    viewModel.add(binding.cbWomen.isChecked.sex())
                }
                1 -> {
                    // 根据姓名查询
                    viewModel.queryByName()
                }
                2 -> {
                    // 查询全部
                    viewModel.queryAll()
                }
            }
        }

        binding.etName.drawBackground()
            .isDrawBackground(true)
            .setBackgroundColorRes(android.R.color.transparent)
            .setBorderColorStr("#9450F0")
            .setBorderWidth(SizeUtils.dp2px(1f))
            .setRoundedCorners(SizeUtils.dp2px(24f))
            .build()
        binding.etAge.drawBackground()
            .isDrawBackground(true)
            .setBackgroundColorRes(android.R.color.transparent)
            .setBorderColorStr("#9450F0")
            .setBorderWidth(SizeUtils.dp2px(1f))
            .setRoundedCorners(SizeUtils.dp2px(24f))
            .build()

        binding.cbMan.isChecked = true
        binding.cbMan.setOnCheckedChangeListener { _, check ->
            binding.cbWomen.isChecked = !check
        }
        binding.cbWomen.setOnCheckedChangeListener { _, check ->
            binding.cbMan.isChecked = !check
        }
    }

    private fun Boolean.sex(): Int = if (this) 0 else 1
}