import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun isScanPhotoEnabled():Boolean{
    return true
    //return isCurrentLanguageChinese()
}

@Composable
fun isCurrentLanguageChinese(): Boolean {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales.get(0) // 获取第一个活跃的区域设置，通常是最优先的

    return locale.language == "zh"
}

// 使用示例
@Composable
fun LanguageCheckExample() {
    if (isCurrentLanguageChinese()) {
        Text("当前语言是中文")
    } else {
        Text("当前语言不是中文")
    }
}
