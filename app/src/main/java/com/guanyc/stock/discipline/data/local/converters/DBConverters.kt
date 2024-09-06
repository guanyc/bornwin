package com.guanyc.stock.discipline.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guanyc.stock.discipline.domain.model.StockTarget

import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import com.guanyc.stock.discipline.util.diary.Mood

class DBConverters {

    @TypeConverter
    fun fromTabEntity(value: TabEntity?): String {
        val gson = Gson()
        val type = object : TypeToken<TabEntity>() {}.type

        if(value==null){
            return ""
        }else{
            return gson.toJson(value, type)
        }
    }

    @TypeConverter
    fun toTabEntity(value: String): TabEntity? {
        val gson = Gson()
        val type = object : TypeToken<TabEntity>() {}.type
        if(value.isEmpty()){
            return gson.fromJson("", type)
        }else{
            return gson.fromJson(value, type)
        }

    }

    @TypeConverter
    fun fromListStockTarget(value: List<StockTarget>):String{
        val gson = Gson()
        val type = object : TypeToken<List<StockTarget>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toListStockTarget(value: String): List<StockTarget> {
        val gson = Gson()
        val type = object : TypeToken<List<StockTarget>>() {}.type
        return gson.fromJson(value, type)
    }





    @TypeConverter
    fun fromTabEntitysList(value: List<TabEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<TabEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toTabEntitysList(value: String): List<TabEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<TabEntity>>() {}.type
        return gson.fromJson(value, type)
    }




    @TypeConverter
    fun toMood(value: Int) = enumValues<Mood>()[value]

    @TypeConverter
    fun fromMood(value: Mood) = value.ordinal


}