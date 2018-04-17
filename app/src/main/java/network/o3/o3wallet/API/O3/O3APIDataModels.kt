package network.o3.o3wallet.API.O3

import com.google.gson.JsonArray
import com.google.gson.JsonObject

/**
 * Created by drei on 11/24/17.
 */

data class O3Response(var code: Int, var result: JsonObject)


data class PriceData (val currency: String,
                      val average: Double,
                      val averageUSD: Double,
                      val averageBTC: Double,
                      val time: String)

data class PriceHistory(val symbol: String,
                        val currency: String,
                        val data: Array<PriceData>)

data class Portfolio(val price: Map<String, PriceData>,
                          val firstPrice: Map<String, PriceData>,
                          val data: Array<PriceData>)

data class FeedData(val features: JsonArray, val items: Array<FeedItem>)

data class NewsImage(val title: String, val url: String)

data class FeedItem(val title: String,
                    val description: String,
                    val link: String,
                    val published: String,
                    val source: String,
                    val images: Array<NewsImage>)

data class FeatureFeed(val features: Array<Feature>)

data class Feature(val category: String,
                   val title: String,
                   val subtitle: String,
                   val imageURL: String,
                   val createdAt: Int,
                   val index: Int,
                   val actionTitle: String,
                   val actionURL: String)

data class TokenSales(val live: Array<TokenSale>)

data class TokenSale(val name: String,
                     val symbol: String,
                     val scriptHash: String,
                     val webURL: String,
                     val imageURL: String,
                     val startTime: Long,
                     val endTime: Long,
                     val acceptingAssets: Array<AcceptingAsset>,
                     val info: Array<InfoRow>,
                     val footer: Array<FooterRow>)

data class AcceptingAsset(val asset: String,
                          val basicRate: Long,
                          val min: Long,
                          val max: Long)

data class InfoRow(val label: String,
                   val value: String)

data class FooterRow(val label: String,
                     val value: String,
                     val link: String)
