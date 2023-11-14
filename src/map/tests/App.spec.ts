import { test, expect } from "@playwright/test";

// confirms that playwright and tests functions using a numeric equation
test("is 1 + 1 = 2?", () => {
    expect(1 + 1).toBe(2);
  });

const url = "http://localhost:5173";

test("title is as expected", async ({page}) => {
  await page.goto(url); 
  await expect(page.getByText("Maps")).toBeVisible();
});