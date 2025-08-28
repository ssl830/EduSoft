pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/discussions"
LOGIN_URL = "http://localhost:3000/login"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
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

def test_discussion_list_render(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".discussion-list-page")).to_be_visible(timeout=10000)
    # 判断是否有讨论卡片或空状态
    if page.locator(".discussion-card").count() > 0:
        expect(page.locator(".discussion-card")).to_be_visible()
        expect(page.locator(".discussion-card .discussion-title")).first.to_be_visible()
    else:
        # 如果没有讨论卡片，断言空状态显示
        expect(page.locator(".empty-state")).to_be_visible()
    context.close()
    browser.close()

def test_discussion_search(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".discussion-list-page")).to_be_visible(timeout=10000)
    search_input = page.locator(".search-input")
    search_input.fill("测试")
    page.locator(".search-btn").click()
    # 搜索后判断是否有讨论卡片或空状态
    if page.locator(".discussion-card").count() > 0:
        expect(page.locator(".discussion-card")).to_be_visible()
    else:
        expect(page.locator(".empty-state")).to_be_visible()
    context.close()
    browser.close()

def test_reply_discussion_flow(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".discussion-list-page")).to_be_visible(timeout=10000)
    reply_btn = page.locator(".discussion-card .reply-btn").first
    if reply_btn.is_visible():
        reply_btn.click()
        expect(page.locator(".reply-input-section")).to_be_visible()
        page.locator(".reply-textarea").fill("自动化测试回复内容")
        page.locator(".submit-reply-btn").click()
        expect(page.locator(".status-toast.success")).to_be_visible(timeout=5000)
    context.close()
    browser.close()
