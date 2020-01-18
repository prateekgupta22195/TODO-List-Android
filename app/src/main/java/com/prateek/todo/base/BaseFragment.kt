package com.prateek.todo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getInflatedView(getLayoutId(),inflater,container)
    }

    private fun getInflatedView(layoutId: Int, inflater : LayoutInflater, container : ViewGroup?): View? {
        return inflater.inflate(layoutId, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewInflated(view,savedInstanceState)
    }

    abstract fun onViewInflated(view: View, savedInstanceState: Bundle?)

    abstract fun getLayoutId() : Int

    fun setupDaggerComponent(){}

}