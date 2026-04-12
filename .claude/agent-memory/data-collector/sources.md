---
name: Sources
description: Reliable data sources for specific makes and data types
type: reference
---

## Volkswagen Golf generations

- Wikipedia "Volkswagen Golf" article — authoritative for generation code names (Typ 17, 19E, 1H, 1J, 1K, 5K, 5G, CD), year ranges, and facelift details.
- autoevolution.com — good for production year confirmation and body style variants.

## Toyota Corolla generations (E120-E210)

- Wikipedia "Toyota Corolla (E120)" through "Toyota Corolla (E210)" articles — authoritative for generation codes, year ranges, body style variants, and engine codes per market.
- autoevolution.com — good for transmission options and power figures cross-check.
- Key data gap: torque figures for older gen engines (E90/E100 era) are not always published in English sources; use manufacturer spec sheets when available.
- E130 note: this code is a market-specific internal designation (ASEAN/Middle East) for what is globally the same E120 9th-gen platform. Treat as a regional variant.
- E180 note: China-only designation; mechanically similar to E160/E170 but with longer wheelbase sedan only.
- Hybrid power figures: systemPowerKw lives on Variant (not on Engine). Record ICE engine kW in the Engine row, combined system output in Variant.systemPowerKw.

## Toyota Camry generations (XV50, XV70)

- Wikipedia "Toyota Camry (XV50)" and "Toyota Camry (XV70)" articles — authoritative for generation codes, year ranges, engine codes.
- XV50: 2.5L 2AR-FE (petrol), 2.5L 2AR-FXE + electric 2MZ-FXE (hybrid), 3.5L 2GR-FE (V6). AT6 standard, AT8 on V6.
- XV70: 2.5L A25A-FKS (petrol AT8), 2.5L A25A-FXS + 4NM-FXE electric (hybrid CVT), 3.5L 2GR-FKS (V6 US only). Hybrid systemPower = 160kW.
- XV70 sedan dimensions: 4885×1840×1445mm, WB 2825mm, trunk 493L.

## Toyota RAV4 generations (XA30, XA40, XA50)

- Wikipedia "Toyota RAV4 (XA30)" through "Toyota RAV4 (XA50)" articles — authoritative for specs.
- XA30 (2005-2012): 2.4L 2AZ-FE, 2.0L 3ZR-FAE, 2.2D 2AD-FHV. AT4 dominant; EU got diesel with AT6.
- XA40 (2012-2018): 2.5L 2AR-FE (US/AU), 2.0L 3ZR-FE (EU), 2.2D 2AD-FTV (EU). AT6/CVT.
- XA50 (2018-present): 2.0L M20A-FKS, 2.5L A25A-FKS, 2.5 hybrid A25A-FXS + 3NM-FXE (front) + 1MM-FXE (rear AWD-i). systemPower = 163kW for hybrid.

## General notes

- Wikipedia is the fastest single source for generation names and year ranges for European mainstream brands.
- Official VW press/media sites confirm current-gen specs but often don't list historical generations.
- For Japanese domestic market (JDM) engine variants, skip if not confirmed for European/global market — too many regional-only codes exist.
- Electric motors for Toyota hybrids use NM-FXE / MM-FXE naming convention. displacementCc, torqueNm, cylinderCount can now be OMITTED for electric motors (schema updated to Integer/nullable). No workaround values needed.
- Corolla E210 Hybrid electric motor code: 6NM, powerKw=53, torqueNm=163 (front motor only, FWD or AWD-i).
- RAV4 XA50 Hybrid uses 3-motor config: A25A-FXS (ICE) + 3NM-FXE (front, 88kW) + 1MM-FXE (rear, 40kW). All three linked to same variant. systemPowerKw=163.
- Camry XV40 hybrid systemPowerKw: 147kW combined. XV50: 151kW. XV70: 160kW.
- XV70 V6 (2GR-FKS) uses AWD drivetrain in the model to avoid uniqueness conflict with 2.5L AT8 FWD variant.
- Corolla E210 Hybrid: systemPowerKw=90 (1.8L ICE + 6NM electric). AWD drivetrain used for sedan/wagon hybrid variants to separate from petrol CVT FWD.
