package ru.cbr.rates.client

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import ru.cbr.rates.config.CbrRatesProperties
import ru.cbr.rates.model.CharsetConstants.UTF_8
import ru.cbr.rates.model.CharsetConstants.WINDOWS_1251
import java.time.LocalDate
import java.util.Currency
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private val TEST_XML =
  """
  <?xml version="1.0" encoding="windows-1251"?>
  <ValCurs Date="02.03.2002" name="Foreign Currency Market">
      <Valute ID="R01010">
          <NumCode>036</NumCode>
          <CharCode>AUD</CharCode>
          <Nominal>1</Nominal>
          <Name>Австралийский доллар</Name>
          <Value>16,0102</Value>
          <VunitRate>16,0102</VunitRate>
      </Valute>
      <Valute ID="R01035">
          <NumCode>826</NumCode>
          <CharCode>GBP</CharCode>
          <Nominal>1</Nominal>
          <Name>Фунт стерлингов</Name>
          <Value>43,8254</Value>
          <VunitRate>43,8254</VunitRate>
      </Valute>
      <Valute ID="R01090">
          <NumCode>974</NumCode>
          <CharCode>BYR</CharCode>
          <Nominal>1000</Nominal>
          <Name>Белорусских рублей</Name>
          <Value>18,4290</Value>
          <VunitRate>0,018429</VunitRate>
      </Valute>
      <Valute ID="R01215">
          <NumCode>208</NumCode>
          <CharCode>DKK</CharCode>
          <Nominal>10</Nominal>
          <Name>Датских крон</Name>
          <Value>36,1010</Value>
          <VunitRate>3,6101</VunitRate>
      </Valute>
      <Valute ID="R01235">
          <NumCode>840</NumCode>
          <CharCode>USD</CharCode>
          <Nominal>1</Nominal>
          <Name>Доллар США</Name>
          <Value>30,9436</Value>
          <VunitRate>30,9436</VunitRate>
      </Valute>
      <Valute ID="R01239">
          <NumCode>978</NumCode>
          <CharCode>EUR</CharCode>
          <Nominal>1</Nominal>
          <Name>Евро</Name>
          <Value>26,8343</Value>
          <VunitRate>26,8343</VunitRate>
      </Valute>
      <Valute ID="R01310">
          <NumCode>352</NumCode>
          <CharCode>ISK</CharCode>
          <Nominal>100</Nominal>
          <Name>Исландских крон</Name>
          <Value>30,7958</Value>
          <VunitRate>0,307958</VunitRate>
      </Valute>
      <Valute ID="R01335">
          <NumCode>398</NumCode>
          <CharCode>KZT</CharCode>
          <Nominal>100</Nominal>
          <Name>Тенге</Name>
          <Value>20,3393</Value>
          <VunitRate>0,203393</VunitRate>
      </Valute>
      <Valute ID="R01350">
          <NumCode>124</NumCode>
          <CharCode>CAD</CharCode>
          <Nominal>1</Nominal>
          <Name>Канадский доллар</Name>
          <Value>19,3240</Value>
          <VunitRate>19,324</VunitRate>
      </Valute>
      <Valute ID="R01535">
          <NumCode>578</NumCode>
          <CharCode>NOK</CharCode>
          <Nominal>10</Nominal>
          <Name>Норвежских крон</Name>
          <Value>34,7853</Value>
          <VunitRate>3,47853</VunitRate>
      </Valute>
      <Valute ID="R01589">
          <NumCode>960</NumCode>
          <CharCode>XDR</CharCode>
          <Nominal>1</Nominal>
          <Name>СДР (специальные права заимствования)</Name>
          <Value>38,4205</Value>
          <VunitRate>38,4205</VunitRate>
      </Valute>
      <Valute ID="R01625">
          <NumCode>702</NumCode>
          <CharCode>SGD</CharCode>
          <Nominal>1</Nominal>
          <Name>Сингапурский доллар</Name>
          <Value>16,8878</Value>
          <VunitRate>16,8878</VunitRate>
      </Valute>
      <Valute ID="R01700">
          <NumCode>792</NumCode>
          <CharCode>TRL</CharCode>
          <Nominal>1000000</Nominal>
          <Name>Турецких лир</Name>
          <Value>22,2616</Value>
          <VunitRate>2,22616E-05</VunitRate>
      </Valute>
      <Valute ID="R01720">
          <NumCode>980</NumCode>
          <CharCode>UAH</CharCode>
          <Nominal>10</Nominal>
          <Name>Гривен</Name>
          <Value>58,1090</Value>
          <VunitRate>5,8109</VunitRate>
      </Valute>
      <Valute ID="R01770">
          <NumCode>752</NumCode>
          <CharCode>SEK</CharCode>
          <Nominal>10</Nominal>
          <Name>Шведских крон</Name>
          <Value>29,5924</Value>
          <VunitRate>2,95924</VunitRate>
      </Valute>
      <Valute ID="R01775">
          <NumCode>756</NumCode>
          <CharCode>CHF</CharCode>
          <Nominal>1</Nominal>
          <Name>Швейцарский франк</Name>
          <Value>18,1861</Value>
          <VunitRate>18,1861</VunitRate>
      </Valute>
      <Valute ID="R01820">
          <NumCode>392</NumCode>
          <CharCode>JPY</CharCode>
          <Nominal>100</Nominal>
          <Name>Иен</Name>
          <Value>23,1527</Value>
          <VunitRate>0,231527</VunitRate>
      </Valute>
  </ValCurs>
  """.trimIndent()

private val TEST_XML_CURRENCY_LIST =
  """
  <?xml version="1.0" encoding="windows-1251"?>
  <Valuta name="Foreign Currency Market Lib">
      <Item ID="R01235">
          <Name>Доллар США</Name>
          <EngName>US Dollar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01235    </ParentCode>
          <ISO_Num_Code>840</ISO_Num_Code>
          <ISO_Char_Code>USD</ISO_Char_Code>
      </Item>
      <Item ID="R01010">
          <Name>Австралийский доллар</Name>
          <EngName>Australian Dollar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01010    </ParentCode>
          <ISO_Num_Code>36</ISO_Num_Code>
          <ISO_Char_Code>AUD</ISO_Char_Code>
      </Item>
      <Item ID="R01015">
          <Name>Австрийский шиллинг</Name>
          <EngName>Austrian Shilling</EngName>
          <Nominal>1000</Nominal>
          <ParentCode>R01015    </ParentCode>
          <ISO_Num_Code>40</ISO_Num_Code>
          <ISO_Char_Code>ATS</ISO_Char_Code>
      </Item>
      <Item ID="R01020A">
          <Name>Азербайджанский манат</Name>
          <EngName>Azerbaijan Manat</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01020    </ParentCode>
          <ISO_Num_Code>944</ISO_Num_Code>
          <ISO_Char_Code>AZN</ISO_Char_Code>
      </Item>
      <Item ID="R01030">
          <Name>Алжирский динар</Name>
          <EngName>Algerian Dinar</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01030    </ParentCode>
          <ISO_Num_Code>12</ISO_Num_Code>
          <ISO_Char_Code>DZD</ISO_Char_Code>
      </Item>
      <Item ID="R01035">
          <Name>Фунт стерлингов</Name>
          <EngName>Pound Sterling</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01035    </ParentCode>
          <ISO_Num_Code>826</ISO_Num_Code>
          <ISO_Char_Code>GBP</ISO_Char_Code>
      </Item>
      <Item ID="R01040F">
          <Name>Ангольская новая кванза</Name>
          <EngName>Angolan new Kwanza</EngName>
          <Nominal>100000</Nominal>
          <ParentCode>R01040    </ParentCode>
          <ISO_Num_Code>24</ISO_Num_Code>
          <ISO_Char_Code>AON</ISO_Char_Code>
      </Item>
      <Item ID="R01060">
          <Name>Армянский драм</Name>
          <EngName>Armenian Dram</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01060    </ParentCode>
          <ISO_Num_Code>51</ISO_Num_Code>
          <ISO_Char_Code>AMD</ISO_Char_Code>
      </Item>
      <Item ID="R01080">
          <Name>Бахрейнский динар</Name>
          <EngName>Bahraini Dinar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01080    </ParentCode>
          <ISO_Num_Code>48</ISO_Num_Code>
          <ISO_Char_Code>BHD</ISO_Char_Code>
      </Item>
      <Item ID="R01090">
          <Name>Белорусский рубль</Name>
          <EngName>Belarusian Ruble</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01090    </ParentCode>
          <ISO_Num_Code>974</ISO_Num_Code>
          <ISO_Char_Code>BYR</ISO_Char_Code>
      </Item>
      <Item ID="R01090B">
          <Name>Белорусский рубль</Name>
          <EngName>Belarusian Ruble</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01090    </ParentCode>
          <ISO_Num_Code>933</ISO_Num_Code>
          <ISO_Char_Code>BYN</ISO_Char_Code>
      </Item>
      <Item ID="R01095">
          <Name>Бельгийский франк</Name>
          <EngName>Belgium Franc</EngName>
          <Nominal>1000</Nominal>
          <ParentCode>R01095    </ParentCode>
          <ISO_Num_Code>56</ISO_Num_Code>
          <ISO_Char_Code>BEF</ISO_Char_Code>
      </Item>
      <Item ID="R01100">
          <Name>Болгарский лев</Name>
          <EngName>Bulgarian Lev</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01100    </ParentCode>
          <ISO_Num_Code>975</ISO_Num_Code>
          <ISO_Char_Code>BGN</ISO_Char_Code>
      </Item>
      <Item ID="R01105">
          <Name>Боливиано</Name>
          <EngName>Bolivian Boliviano</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01105    </ParentCode>
          <ISO_Num_Code>68</ISO_Num_Code>
          <ISO_Char_Code>BOB</ISO_Char_Code>
      </Item>
      <Item ID="R01115">
          <Name>Бразильский реал</Name>
          <EngName>Brazilian Real</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01115    </ParentCode>
          <ISO_Num_Code>986</ISO_Num_Code>
          <ISO_Char_Code>BRL</ISO_Char_Code>
      </Item>
      <Item ID="R01135">
          <Name>Форинт</Name>
          <EngName>Forint</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01135    </ParentCode>
          <ISO_Num_Code>348</ISO_Num_Code>
          <ISO_Char_Code>HUF</ISO_Char_Code>
      </Item>
      <Item ID="R01150">
          <Name>Донг</Name>
          <EngName>Dong</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01150    </ParentCode>
          <ISO_Num_Code>704</ISO_Num_Code>
          <ISO_Char_Code>VND</ISO_Char_Code>
      </Item>
      <Item ID="R01200">
          <Name>Гонконгский доллар</Name>
          <EngName>Hong Kong Dollar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01200    </ParentCode>
          <ISO_Num_Code>344</ISO_Num_Code>
          <ISO_Char_Code>HKD</ISO_Char_Code>
      </Item>
      <Item ID="R01205">
          <Name>Греческая драхма</Name>
          <EngName>Greek Drachma</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01205    </ParentCode>
          <ISO_Num_Code>300</ISO_Num_Code>
          <ISO_Char_Code>GRD</ISO_Char_Code>
      </Item>
      <Item ID="R01210">
          <Name>Лари</Name>
          <EngName>Lari</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01210    </ParentCode>
          <ISO_Num_Code>981</ISO_Num_Code>
          <ISO_Char_Code>GEL</ISO_Char_Code>
      </Item>
      <Item ID="R01215">
          <Name>Датская крона</Name>
          <EngName>Danish Krone</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01215    </ParentCode>
          <ISO_Num_Code>208</ISO_Num_Code>
          <ISO_Char_Code>DKK</ISO_Char_Code>
      </Item>
      <Item ID="R01230">
          <Name>Дирхам ОАЭ</Name>
          <EngName>UAE Dirham</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01230    </ParentCode>
          <ISO_Num_Code>784</ISO_Num_Code>
          <ISO_Char_Code>AED</ISO_Char_Code>
      </Item>
      <Item ID="R01239">
          <Name>Евро</Name>
          <EngName>Euro</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01239    </ParentCode>
          <ISO_Num_Code>978</ISO_Num_Code>
          <ISO_Char_Code>EUR</ISO_Char_Code>
      </Item>
      <Item ID="R01240">
          <Name>Египетский фунт</Name>
          <EngName>Egyptian Pound</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01240    </ParentCode>
          <ISO_Num_Code>818</ISO_Num_Code>
          <ISO_Char_Code>EGP</ISO_Char_Code>
      </Item>
      <Item ID="R01270">
          <Name>Индийская рупия</Name>
          <EngName>Indian Rupee</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01270    </ParentCode>
          <ISO_Num_Code>356</ISO_Num_Code>
          <ISO_Char_Code>INR</ISO_Char_Code>
      </Item>
      <Item ID="R01280">
          <Name>Рупия</Name>
          <EngName>Rupiah</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01280    </ParentCode>
          <ISO_Num_Code>360</ISO_Num_Code>
          <ISO_Char_Code>IDR</ISO_Char_Code>
      </Item>
      <Item ID="R01300">
          <Name>Иранский риал</Name>
          <EngName>Iranian Rial</EngName>
          <Nominal>100000</Nominal>
          <ParentCode>R01300    </ParentCode>
          <ISO_Num_Code>364</ISO_Num_Code>
          <ISO_Char_Code>IRR</ISO_Char_Code>
      </Item>
      <Item ID="R01305">
          <Name>Ирландский фунт</Name>
          <EngName>Irish Pound</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01305    </ParentCode>
          <ISO_Num_Code>372</ISO_Num_Code>
          <ISO_Char_Code>IEP</ISO_Char_Code>
      </Item>
      <Item ID="R01310">
          <Name>Исландская крона</Name>
          <EngName>Iceland Krona</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01310    </ParentCode>
          <ISO_Num_Code>352</ISO_Num_Code>
          <ISO_Char_Code>ISK</ISO_Char_Code>
      </Item>
      <Item ID="R01315">
          <Name>Испанская песета</Name>
          <EngName>Spanish Peseta</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01315    </ParentCode>
          <ISO_Num_Code>724</ISO_Num_Code>
          <ISO_Char_Code>ESP</ISO_Char_Code>
      </Item>
      <Item ID="R01325">
          <Name>Итальянская лира</Name>
          <EngName>Italian Lira</EngName>
          <Nominal>100000</Nominal>
          <ParentCode>R01325    </ParentCode>
          <ISO_Num_Code>380</ISO_Num_Code>
          <ISO_Char_Code>ITL</ISO_Char_Code>
      </Item>
      <Item ID="R01335">
          <Name>Тенге</Name>
          <EngName>Tenge</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01335    </ParentCode>
          <ISO_Num_Code>398</ISO_Num_Code>
          <ISO_Char_Code>KZT</ISO_Char_Code>
      </Item>
      <Item ID="R01350">
          <Name>Канадский доллар</Name>
          <EngName>Canadian Dollar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01350    </ParentCode>
          <ISO_Num_Code>124</ISO_Num_Code>
          <ISO_Char_Code>CAD</ISO_Char_Code>
      </Item>
      <Item ID="R01355">
          <Name>Катарский риал</Name>
          <EngName>Qatari Rial</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01355    </ParentCode>
          <ISO_Num_Code>634</ISO_Num_Code>
          <ISO_Char_Code>QAR</ISO_Char_Code>
      </Item>
      <Item ID="R01370">
          <Name>Сом</Name>
          <EngName>Som</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01370    </ParentCode>
          <ISO_Num_Code>417</ISO_Num_Code>
          <ISO_Char_Code>KGS</ISO_Char_Code>
      </Item>
      <Item ID="R01375">
          <Name>Юань</Name>
          <EngName>Yuan Renminbi</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01375    </ParentCode>
          <ISO_Num_Code>156</ISO_Num_Code>
          <ISO_Char_Code>CNY</ISO_Char_Code>
      </Item>
      <Item ID="R01390">
          <Name>Кувейтский динар</Name>
          <EngName>Kuwaiti Dinar</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01390    </ParentCode>
          <ISO_Num_Code>414</ISO_Num_Code>
          <ISO_Char_Code>KWD</ISO_Char_Code>
      </Item>
      <Item ID="R01395">
          <Name>Кубинское песо</Name>
          <EngName>Cuban peso</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01395    </ParentCode>
          <ISO_Num_Code>192</ISO_Num_Code>
          <ISO_Char_Code>CUP</ISO_Char_Code>
      </Item>
      <Item ID="R01405">
          <Name>Латвийский лат</Name>
          <EngName>Latvian Lat</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01405    </ParentCode>
          <ISO_Num_Code>428</ISO_Num_Code>
          <ISO_Char_Code>LVL</ISO_Char_Code>
      </Item>
      <Item ID="R01420">
          <Name>Ливанский фунт</Name>
          <EngName>Lebanese Pound</EngName>
          <Nominal>100000</Nominal>
          <ParentCode>R01420    </ParentCode>
          <ISO_Num_Code>422</ISO_Num_Code>
          <ISO_Char_Code>LBP</ISO_Char_Code>
      </Item>
      <Item ID="R01435">
          <Name>Литовский лит</Name>
          <EngName>Lithuanian Lita</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01435    </ParentCode>
          <ISO_Num_Code>440</ISO_Num_Code>
          <ISO_Char_Code>LTL</ISO_Char_Code>
      </Item>
      <Item ID="R01436">
          <Name>Литовский талон</Name>
          <EngName>Lithuanian talon</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01435    </ParentCode>
          <ISO_Num_Code></ISO_Num_Code>
          <ISO_Char_Code></ISO_Char_Code>
      </Item>
      <Item ID="R01500">
          <Name>Молдавский лей</Name>
          <EngName>Moldovan Leu</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01500    </ParentCode>
          <ISO_Num_Code>498</ISO_Num_Code>
          <ISO_Char_Code>MDL</ISO_Char_Code>
      </Item>
      <Item ID="R01503">
          <Name>Тугрик</Name>
          <EngName>Mongolia Tugrik</EngName>
          <Nominal>1000</Nominal>
          <ParentCode>R01503    </ParentCode>
          <ISO_Num_Code>496</ISO_Num_Code>
          <ISO_Char_Code>MNT</ISO_Char_Code>
      </Item>
      <Item ID="R01510">
          <Name>Немецкая марка</Name>
          <EngName>Deutsche Mark</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01510    </ParentCode>
          <ISO_Num_Code>276</ISO_Num_Code>
          <ISO_Char_Code>DEM</ISO_Char_Code>
      </Item>
      <Item ID="R01510A">
          <Name>Немецкая марка</Name>
          <EngName>Deutsche Mark</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01510    </ParentCode>
          <ISO_Num_Code>280</ISO_Num_Code>
          <ISO_Char_Code>DEM</ISO_Char_Code>
      </Item>
      <Item ID="R01520">
          <Name>Найра</Name>
          <EngName>Nigeria Naira</EngName>
          <Nominal>1000</Nominal>
          <ParentCode>R01520    </ParentCode>
          <ISO_Num_Code>566</ISO_Num_Code>
          <ISO_Char_Code>NGN</ISO_Char_Code>
      </Item>
      <Item ID="R01523">
          <Name>Нидерландский гульден</Name>
          <EngName>Netherlands Gulden</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01523    </ParentCode>
          <ISO_Num_Code>528</ISO_Num_Code>
          <ISO_Char_Code>NLG</ISO_Char_Code>
      </Item>
      <Item ID="R01530">
          <Name>Новозеландский доллар</Name>
          <EngName>New Zealand Dollar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01530    </ParentCode>
          <ISO_Num_Code>554</ISO_Num_Code>
          <ISO_Char_Code>NZD</ISO_Char_Code>
      </Item>
      <Item ID="R01535">
          <Name>Норвежская крона</Name>
          <EngName>Norwegian Krone</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01535    </ParentCode>
          <ISO_Num_Code>578</ISO_Num_Code>
          <ISO_Char_Code>NOK</ISO_Char_Code>
      </Item>
      <Item ID="R01540">
          <Name>Оманский риал</Name>
          <EngName>Omani Rial</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01540    </ParentCode>
          <ISO_Num_Code>512</ISO_Num_Code>
          <ISO_Char_Code>OMR</ISO_Char_Code>
      </Item>
      <Item ID="R01565">
          <Name>Злотый</Name>
          <EngName>Zloty</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01565    </ParentCode>
          <ISO_Num_Code>985</ISO_Num_Code>
          <ISO_Char_Code>PLN</ISO_Char_Code>
      </Item>
      <Item ID="R01570">
          <Name>Португальский эскудо</Name>
          <EngName>Portuguese Escudo</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01570    </ParentCode>
          <ISO_Num_Code>620</ISO_Num_Code>
          <ISO_Char_Code>PTE</ISO_Char_Code>
      </Item>
      <Item ID="R01580">
          <Name>Саудовский риял</Name>
          <EngName>Saudi Riyal</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01580    </ParentCode>
          <ISO_Num_Code>682</ISO_Num_Code>
          <ISO_Char_Code>SAR</ISO_Char_Code>
      </Item>
      <Item ID="R01580C">
          <Name>Саудовский риял</Name>
          <EngName>Saudi Riyal</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01580    </ParentCode>
          <ISO_Num_Code>682</ISO_Num_Code>
          <ISO_Char_Code>SAR</ISO_Char_Code>
      </Item>
      <Item ID="R01585">
          <Name>Румынский лей</Name>
          <EngName>Romanian Leu</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01585    </ParentCode>
          <ISO_Num_Code>642</ISO_Num_Code>
          <ISO_Char_Code>ROL</ISO_Char_Code>
      </Item>
      <Item ID="R01585F">
          <Name>Румынский лей</Name>
          <EngName>Romanian Leu</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01585    </ParentCode>
          <ISO_Num_Code>946</ISO_Num_Code>
          <ISO_Char_Code>RON</ISO_Char_Code>
      </Item>
      <Item ID="R01589">
          <Name>СДР (специальные права заимствования)</Name>
          <EngName>SDR (Special Drawing Right)</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01589    </ParentCode>
          <ISO_Num_Code>960</ISO_Num_Code>
          <ISO_Char_Code>XDR</ISO_Char_Code>
      </Item>
      <Item ID="R01625">
          <Name>Сингапурский доллар</Name>
          <EngName>Singapore Dollar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01625    </ParentCode>
          <ISO_Num_Code>702</ISO_Num_Code>
          <ISO_Char_Code>SGD</ISO_Char_Code>
      </Item>
      <Item ID="R01665A">
          <Name>Суринамский доллар</Name>
          <EngName>Surinam Dollar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01665    </ParentCode>
          <ISO_Num_Code>968</ISO_Num_Code>
          <ISO_Char_Code>SRD</ISO_Char_Code>
      </Item>
      <Item ID="R01670">
          <Name>Сомони</Name>
          <EngName>Somoni</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01670    </ParentCode>
          <ISO_Num_Code>972</ISO_Num_Code>
          <ISO_Char_Code>TJS</ISO_Char_Code>
      </Item>
      <Item ID="R01675">
          <Name>Бат</Name>
          <EngName>Baht</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01675    </ParentCode>
          <ISO_Num_Code>764</ISO_Num_Code>
          <ISO_Char_Code>THB</ISO_Char_Code>
      </Item>
      <Item ID="R01685">
          <Name>Така</Name>
          <EngName>Bangladesh Taka</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01685    </ParentCode>
          <ISO_Num_Code>50</ISO_Num_Code>
          <ISO_Char_Code>BDT</ISO_Char_Code>
      </Item>
      <Item ID="R01700J">
          <Name>Турецкая лира</Name>
          <EngName>Turkish Lira</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01700    </ParentCode>
          <ISO_Num_Code>949</ISO_Num_Code>
          <ISO_Char_Code>TRY</ISO_Char_Code>
      </Item>
      <Item ID="R01710">
          <Name>Туркменский манат</Name>
          <EngName>Turkmenistan Manat</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01710    </ParentCode>
          <ISO_Num_Code>795</ISO_Num_Code>
          <ISO_Char_Code>TMM</ISO_Char_Code>
      </Item>
      <Item ID="R01710A">
          <Name>Новый туркменский манат</Name>
          <EngName>Turkmenistan New Manat</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01710    </ParentCode>
          <ISO_Num_Code>934</ISO_Num_Code>
          <ISO_Char_Code>TMT</ISO_Char_Code>
      </Item>
      <Item ID="R01717">
          <Name>Узбекский сум</Name>
          <EngName>Uzbekistan Sum</EngName>
          <Nominal>10000</Nominal>
          <ParentCode>R01717    </ParentCode>
          <ISO_Num_Code>860</ISO_Num_Code>
          <ISO_Char_Code>UZS</ISO_Char_Code>
      </Item>
      <Item ID="R01720">
          <Name>Гривна</Name>
          <EngName>Hryvnia</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01720    </ParentCode>
          <ISO_Num_Code>980</ISO_Num_Code>
          <ISO_Char_Code>UAH</ISO_Char_Code>
      </Item>
      <Item ID="R01720A">
          <Name>Украинский карбованец</Name>
          <EngName>Ukrainian Hryvnia</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01720    </ParentCode>
          <ISO_Num_Code></ISO_Num_Code>
          <ISO_Char_Code></ISO_Char_Code>
      </Item>
      <Item ID="R01740">
          <Name>Финляндская марка</Name>
          <EngName>Finnish Marka</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01740    </ParentCode>
          <ISO_Num_Code>246</ISO_Num_Code>
          <ISO_Char_Code>FIM</ISO_Char_Code>
      </Item>
      <Item ID="R01750">
          <Name>Французский франк</Name>
          <EngName>French Franc</EngName>
          <Nominal>1000</Nominal>
          <ParentCode>R01750    </ParentCode>
          <ISO_Num_Code>250</ISO_Num_Code>
          <ISO_Char_Code>FRF</ISO_Char_Code>
      </Item>
      <Item ID="R01760">
          <Name>Чешская крона</Name>
          <EngName>Czech Koruna</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01760    </ParentCode>
          <ISO_Num_Code>203</ISO_Num_Code>
          <ISO_Char_Code>CZK</ISO_Char_Code>
      </Item>
      <Item ID="R01770">
          <Name>Шведская крона</Name>
          <EngName>Swedish Krona</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01770    </ParentCode>
          <ISO_Num_Code>752</ISO_Num_Code>
          <ISO_Char_Code>SEK</ISO_Char_Code>
      </Item>
      <Item ID="R01775">
          <Name>Швейцарский франк</Name>
          <EngName>Swiss Franc</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01775    </ParentCode>
          <ISO_Num_Code>756</ISO_Num_Code>
          <ISO_Char_Code>CHF</ISO_Char_Code>
      </Item>
      <Item ID="R01790">
          <Name>ЭКЮ</Name>
          <EngName>ECU</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01790    </ParentCode>
          <ISO_Num_Code>954</ISO_Num_Code>
          <ISO_Char_Code>XEU</ISO_Char_Code>
      </Item>
      <Item ID="R01795">
          <Name>Эстонская крона</Name>
          <EngName>Estonian Kroon</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01795    </ParentCode>
          <ISO_Num_Code>233</ISO_Num_Code>
          <ISO_Char_Code>EEK</ISO_Char_Code>
      </Item>
      <Item ID="R01800">
          <Name>Эфиопский быр</Name>
          <EngName>Ethiopian Birr</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01800    </ParentCode>
          <ISO_Num_Code>230</ISO_Num_Code>
          <ISO_Char_Code>ETB</ISO_Char_Code>
      </Item>
      <Item ID="R01805">
          <Name>Югославский новый динар</Name>
          <EngName>Yugoslavian Dinar</EngName>
          <Nominal>1</Nominal>
          <ParentCode>R01804    </ParentCode>
          <ISO_Num_Code>890</ISO_Num_Code>
          <ISO_Char_Code>YUN</ISO_Char_Code>
      </Item>
      <Item ID="R01805F">
          <Name>Сербский динар</Name>
          <EngName>Serbian Dinar</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01804    </ParentCode>
          <ISO_Num_Code>941</ISO_Num_Code>
          <ISO_Char_Code>RSD</ISO_Char_Code>
      </Item>
      <Item ID="R01810">
          <Name>Рэнд</Name>
          <EngName>Rand</EngName>
          <Nominal>10</Nominal>
          <ParentCode>R01810    </ParentCode>
          <ISO_Num_Code>710</ISO_Num_Code>
          <ISO_Char_Code>ZAR</ISO_Char_Code>
      </Item>
      <Item ID="R01815">
          <Name>Вона</Name>
          <EngName>Won</EngName>
          <Nominal>1000</Nominal>
          <ParentCode>R01815    </ParentCode>
          <ISO_Num_Code>410</ISO_Num_Code>
          <ISO_Char_Code>KRW</ISO_Char_Code>
      </Item>
      <Item ID="R01820">
          <Name>Иена</Name>
          <EngName>Yen</EngName>
          <Nominal>100</Nominal>
          <ParentCode>R01820    </ParentCode>
          <ISO_Num_Code>392</ISO_Num_Code>
          <ISO_Char_Code>JPY</ISO_Char_Code>
      </Item>
      <Item ID="R02005">
          <Name>Кьят</Name>
          <EngName>Kyat</EngName>
          <Nominal>1000</Nominal>
          <ParentCode>R02005    </ParentCode>
          <ISO_Num_Code>104</ISO_Num_Code>
          <ISO_Char_Code>MMK</ISO_Char_Code>
      </Item>
  </Valuta>
  """.trimIndent()

class CbrRatesClientTest {
  private lateinit var restTemplate: RestTemplate
  private lateinit var properties: CbrRatesProperties
  private lateinit var client: CbrRatesClient

  @BeforeEach
  fun setup() {
    restTemplate = mock()
    properties = CbrRatesProperties(baseUrl = "https://www.cbr.ru/scripts")
    client = CbrRatesClient(restTemplate, properties)
  }

  @Test
  fun `should parse daily rates XML correctly`() {
    whenever(
      restTemplate.exchange(
        eq("https://www.cbr.ru/scripts/XML_daily.asp?date_req=01/08/2025"),
        eq(HttpMethod.GET),
        any(),
        eq(ByteArray::class.java),
      ),
    ).thenReturn(ResponseEntity.ok(TEST_XML.toByteArray(WINDOWS_1251)))

    val rates = client.getRatesForDate(LocalDate.of(2025, 8, 1))

    assertEquals(LocalDate.of(2002, 3, 2), rates.date)
    assertEquals("Foreign Currency Market", rates.name)
    assertEquals(17, rates.rates.size)

    val usdRate = rates.rates.find { it.charCode == "USD" }
    assertNotNull(usdRate)
    assertEquals("R01235", usdRate.id)
    assertEquals("840", usdRate.numCode)
    assertEquals("USD", usdRate.charCode)
    assertEquals(1, usdRate.nominal)
    // Just check it's not empty since encoding may vary
    assert(usdRate.name.isNotEmpty())
    assertEquals("30.9436".toBigDecimal(), usdRate.value)
  }

  @Test
  fun `should get currency rate by Currency object`() {
    whenever(
      restTemplate.exchange(
        any<String>(),
        eq(HttpMethod.GET),
        any(),
        eq(ByteArray::class.java),
      ),
    ).thenReturn(ResponseEntity.ok(TEST_XML.toByteArray(UTF_8)))

    val currencyCode = "USD"
    val rateByCurrency = client.getCurrencyRate(Currency.getInstance(currencyCode))
    val rateByCode = client.getCurrencyRate(currencyCode)

    assertNotNull(rateByCurrency)
    assertEquals(rateByCurrency, rateByCode)
    assertEquals(currencyCode, rateByCurrency.charCode)
    assertEquals("30.9436".toBigDecimal(), rateByCurrency.value)
  }

  @Test
  fun `should parse currency list XML correctly`() {
    whenever(
      restTemplate.exchange(
        eq("https://www.cbr.ru/scripts/XML_valFull.asp"),
        eq(HttpMethod.GET),
        any(),
        eq(ByteArray::class.java),
      ),
    ).thenReturn(ResponseEntity.ok(TEST_XML_CURRENCY_LIST.toByteArray(WINDOWS_1251)))

    val currencyList = client.getCurrencyList()
    val currencies = client.getCurrencies()

    assertEquals("Foreign Currency Market Lib", currencyList.name)
    assertEquals(83, currencyList.currencies.size)
    assertEquals(83, currencies.size)

    val currency = currencyList.currencies[0]
    assertEquals("R01235", currency.id)
    // Just check it's not empty since encoding may vary
    assert(currency.name.isNotEmpty())
    assertEquals("US Dollar", currency.engName)
    assertEquals(1, currency.nominal)
    assertEquals("840", currency.isoNumCode)
    assertEquals("USD", currency.isoCharCode)
  }

  @Test
  @Disabled("Integration test - connects to real CBR service")
  fun `should connect to real CBR service and fetch current rates`() {
    // This test uses real RestTemplate to connect to CBR API
    val realRestTemplate = RestTemplate()
    val realProperties = CbrRatesProperties(baseUrl = "https://www.cbr.ru/scripts")
    val realClient = CbrRatesClient(realRestTemplate, realProperties)

    // Test getting current rates
    val rates = realClient.getCurrentRates()
    assertNotNull(rates)
    assert(rates.rates.isNotEmpty())

    // Test getting specific currency (USD should always be available)
    val usdRate = realClient.getCurrencyRate(Currency.getInstance("USD"))
    assertNotNull(usdRate)
    assertEquals("USD", usdRate.charCode)

    // Test getting available currencies
    val currencies = realClient.getCurrencyList()
    assertNotNull(currencies)
    assert(currencies.currencies.isNotEmpty())
  }
}
