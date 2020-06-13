package com.permissionx.xieman

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

//Kotlin小技巧，typealias关键字可以用于给任意类型指定一个别名，比如我们将(Boolean,List<String>) -> Unit的别名指定成了PermissionCallback，这样就可以使用PermissionCallback代替所有的
//(Boolean,List<String>) -> Unit，从而让代码变得更加简洁易懂。
typealias PermissionCallback = (Boolean,List<String>) -> Unit

class InvisibleFragment : Fragment() {

    //首先定义了一个callback变量作为运行时权限申请结果的回调通知方式，并将它声明成了一种函数类型变量，该函数类型接收Boolean和List<String>这两种类型的参数，并且没有返回值
    private var callback: (PermissionCallback)? = null

    //然后定义了一个requestNow()方法，该方法接收一个与callback变量类型相同的函数类型参数，同时还使用vararg关键字接收一个可变长度的permissions参数列表。
//    在requestNow()方法中，我们将传递进来的函数类型参数赋值给callback变量，然后调用Fragment中提供的requestPermissions()方法去立即申请运行时权限，并将permissions
//    参数列表传递进去，这样就可以实现由外部调用方自主指定要申请哪些权限的功能了。
    fun requestNow(cb:PermissionCallback,vararg permissions: String){
        callback = cb
        requestPermissions(permissions,1)
    }

//    重写onRequestPermissionResult()方法，并在这里处理运行时权限的申请结果。使用一个deniedList列表来记录所有被用户拒绝的权限，然后遍历grantResults数组，如果发现某个权限未被用户授权，就将它添加到deniedList中，
//    遍历结束后使用一个allGranted变量来标识是否所有申请的权限都已被授权，判断的依据是deniedList列表是否为空。最后使用callback变量对运行时权限的申请结果进行回调。
//    注意：我们并没有重写onCreateView()方法来加载某个布局，因此它自然就是一个不可见的Fragment,待会只要将它添加到Activity中即可。
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            val deniedList = ArrayList<String>()
            for ((index,result) in grantResults.withIndex()){
                if (result != PackageManager.PERMISSION_GRANTED){
                    deniedList.add(permissions[index])
                }
            }
            val allGranted = deniedList.isEmpty()
            callback?.let { it(allGranted,deniedList) }
        }
    }
}