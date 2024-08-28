package com.guanyc.stock.discipline

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.presentation.main.components.TAB_TYPE
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import org.junit.Assert
import org.junit.Test
import java.io.StringReader
import java.lang.reflect.Type

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2)
    }
     fun fromTabEntityList(value: List<TabEntity>){
        val t1 = TabEntity(title="追高", tabType = TAB_TYPE.TAB_ACTION)

        val t2 = TabEntity(title="打板", tabType = TAB_TYPE.TAB_ACTION)

        val s1 = StockTarget(stockTargetId = 1,code="1")
         val s2 = StockTarget(stockTargetId = 2, code="2")
         val s3 = StockTarget(stockTargetId = 3,code="3")
         val s4 = StockTarget(stockTargetId = 4,code="4")

         val map1: Map<TabEntity, List<StockTarget>> = mapOf(
             t1 to listOf(s1,s2),
             t2 to listOf(s3,s4)
         )

        val tablist = listOf(t1,t2)

         val gson = Gson()

         val json = gson.toJson(tablist)


         val type: Type = object : TypeToken< Map<TabEntity, List<StockTarget>>>() {}.type

         val jsonmap1: String = gson.toJson(map1, type)


         assert(jsonmap1.contains("\"title\":\"追高\""))

         Assert.assertTrue(json.contains("\"title\":\"追高\""))

         val gson2 = Gson()

         val map2: Map<TabEntity, List<StockTarget>> = gson2.fromJson( StringReader(jsonmap1), type)
         assert(map2.containsKey(t1))
         assert(map2.containsKey(t2))

         val emptyMap = mapOf<TabEntity, List<StockTarget>>()
         val jsonEmptyMap: String = gson.toJson(emptyMap, type)
         assert(!jsonEmptyMap.contains("\"title\":\"追高\""))
         //val mapFromJson: Map<TabEntity, List<StockTarget>> =  gson2.fromJson( StringReader(jsonEmptyMap), type)

         val mapFromJson: Map<TabEntity, List<StockTarget>> =  gson2.fromJson(jsonEmptyMap, type)
         assert(!mapFromJson.containsKey(t1))

         //emptyMap.map { (k, v) ->k to v.toMutableList() }.toMap().toMutableMap()

    }
}