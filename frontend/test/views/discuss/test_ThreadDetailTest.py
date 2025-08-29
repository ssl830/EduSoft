pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/discussions/1"
LOGIN_URL = "http://localhost:3000/login"
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

def test_thread_detail_render(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".thread-detail-page")).to_be_visible(timeout=10000)
    expect(page.locator(".thread-title")).to_be_visible()
    expect(page.locator(".thread-content")).to_be_visible()
    context.close()
    browser.close()

def test_thread_reply_flow(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".thread-detail-page")).to_be_visible(timeout=10000)
    # 回复表单可见
    expect(page.locator(".reply-form-container")).to_be_visible()
    textarea = page.locator(".reply-form textarea")
    textarea.fill("自动化测试回复内容")
    expect(textarea).to_have_value("自动化测试回复内容")
    page.locator(".reply-form .submit-button").click()
    # 提交后可见成功提示或新回复内容
    success_toast = page.locator(".status-toast.success")
    reply_content = page.locator(".reply-list").locator(":text('自动化测试回复内容')")
    try:
        expect(success_toast).to_be_visible(timeout=5000)
    except Exception:
        # 如果没有 toast，检查回复内容是否已显示
        assert reply_content.count() > 0
    context.close()
    browser.close()
