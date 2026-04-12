import type { ImageSource } from "../types/imageSource";

const BASE = "/api/v1";

export async function fetchImageSource(imageUrl: string): Promise<ImageSource | null> {
  const response = await fetch(
    `${BASE}/image-sources/by-url?imageUrl=${encodeURIComponent(imageUrl)}`
  );
  if (response.status === 404) return null;
  if (!response.ok) {
    throw new Error(`API error: ${response.status} ${response.statusText}`);
  }
  return response.json();
}
