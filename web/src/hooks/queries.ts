import { useQuery } from "@tanstack/react-query";
import {
  fetchMakes,
  fetchMakeBySlug,
  fetchModels,
  fetchGenerations,
  fetchBodies,
  fetchEngines,
  fetchTransmissions,
  fetchVariants,
} from "../api/client";
import type { FuelType, TransmissionType } from "../types/api";

export function useMakes() {
  return useQuery({
    queryKey: ["makes"],
    queryFn: fetchMakes,
    staleTime: 5 * 60 * 1000,
  });
}

export function useMakeBySlug(slug: string) {
  return useQuery({
    queryKey: ["makes", "slug", slug],
    queryFn: () => fetchMakeBySlug(slug),
    staleTime: 5 * 60 * 1000,
    enabled: !!slug,
  });
}

export function useModels(makeId: string | undefined) {
  return useQuery({
    queryKey: ["models", { makeId }],
    queryFn: () => fetchModels(makeId!),
    staleTime: 2 * 60 * 1000,
    enabled: !!makeId,
  });
}

export function useGenerations(modelId: string | undefined) {
  return useQuery({
    queryKey: ["generations", { modelId }],
    queryFn: () => fetchGenerations(modelId!),
    staleTime: 2 * 60 * 1000,
    enabled: !!modelId,
  });
}

export function useBodies(generationId: string | undefined) {
  return useQuery({
    queryKey: ["bodies", { generationId }],
    queryFn: () => fetchBodies(generationId!),
    staleTime: 2 * 60 * 1000,
    enabled: !!generationId,
  });
}

export function useVariants(bodyId: string | undefined) {
  return useQuery({
    queryKey: ["variants", { bodyId }],
    queryFn: () => fetchVariants(bodyId!),
    staleTime: 2 * 60 * 1000,
    enabled: !!bodyId,
  });
}

export function useEngines(fuelType?: FuelType) {
  return useQuery({
    queryKey: ["engines", { fuelType }],
    queryFn: () => fetchEngines(fuelType),
    staleTime: 5 * 60 * 1000,
  });
}

export function useTransmissions(type?: TransmissionType) {
  return useQuery({
    queryKey: ["transmissions", { type }],
    queryFn: () => fetchTransmissions(type),
    staleTime: 5 * 60 * 1000,
  });
}
