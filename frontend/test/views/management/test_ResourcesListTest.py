pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/manage/resources"
LOGIN_URL = "http://localhost:3000/login"
TUTOR_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch(headless=True, args=["--no-sandbox"])
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if goto_url:
        if page.url.startswith("http://localhost:3000/login?redirect="):
            page.goto(goto_url)
        else:
            page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_resources_list_render(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".resource-list-page")).to_be_visible(timeout=10000)
    expect(page.locator(".filters-bar")).to_be_visible()
    expect(page.locator(".filters-card")).to_be_visible()
    expect(page.locator(".filter-group label:has-text('教师：')")).to_be_visible()
    expect(page.locator(".filter-group label:has-text('课程：')")).to_be_visible()
    context.close()
    browser.close()

def test_resources_list_select_teacher_course(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".resource-list-page")).to_be_visible(timeout=10000)
    teacher_select = page.locator("select").first
    teacher_select.select_option(index=1)
    course_select = page.locator("select").nth(1)
    course_select.select_option(index=1)
    # 资源列表组件渲染
    expect(page.locator(".resource-list-page .empty-hint")).not_to_be_visible()
    context.close()
    browser.close()

